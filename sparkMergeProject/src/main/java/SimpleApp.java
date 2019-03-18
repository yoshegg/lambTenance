/* SimpleApp.java */

import java.util.*;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.streaming.Durations;

public class SimpleApp {

    /***************************
     * KAFKA
     **************************/
    private static Properties _kafkaConfigProperties = new Properties();
    private static Producer _kafkaProducer;

    public static void main(String[] args) throws InterruptedException {

        //producer configs
        setUpKafka();

        // Configure Spark
        SparkConf conf = new SparkConf()
                .setAppName("kafka-spark")
                .setMaster("local[*]")
                .set("spark.streaming.concurrentJobs", "10");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(2));

        // Set up Kafka parameters for spark consumer
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "localhost:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "default");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        // Set up topic
        Collection<String> topics = Arrays.asList("speedView", "batchview");

        // create input stream from kafka source
        JavaInputDStream<ConsumerRecord<String, String>> kafkaInStream = createKafkaStream(jssc, topics, kafkaParams);

        // Get the lines of the stream
        JavaDStream<String> lines = kafkaInStream.map(ConsumerRecord::value);

        JavaDStream<String> eAverage = lines.reduce((x, y) -> ((Float.parseFloat(x) + Float.parseFloat(y)) / 2 + ""));

        eAverage.foreachRDD(rddSend ->
                rddSend.foreach(p -> {
                    System.out.println("WERT von AVERAGE: " + p);
                    ProducerRecord<String, String> speedView
                            = new ProducerRecord<>("result", p);
                    _kafkaProducer.send(speedView);
                })
        );

        // Start the computation
        jssc.start();
        jssc.awaitTermination();
    }

    public static JavaInputDStream<ConsumerRecord<String, String>> createKafkaStream(JavaStreamingContext jssc, Collection<String> topics, Map<String, Object> kafkaParams) {
        JavaInputDStream<ConsumerRecord<String, String>> stream =

                KafkaUtils.createDirectStream(
                        jssc,
                        LocationStrategies.PreferConsistent(),
                        ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams)
                );
        return stream;
    }

    private static void setUpKafka() {
        _kafkaConfigProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        _kafkaConfigProperties.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer");
        _kafkaConfigProperties.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        _kafkaProducer = new KafkaProducer<String, String>(_kafkaConfigProperties);
    }

}