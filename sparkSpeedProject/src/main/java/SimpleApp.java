/* SimpleApp.java */

import java.util.*;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.sql.SparkSession;
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
        Collection<String> topics = Arrays.asList("maintenance");

        // create input stream from kafka source
        JavaInputDStream<ConsumerRecord<String, String>> kafkaInStream = createKafkaStream(jssc, topics, kafkaParams);

        /*************************************************
         * USE CASE 1 : energy consumption monitoring
         *************************************************/

        // Get the lines, split them into words, count the words and print
        JavaDStream<String> lines = kafkaInStream.map(ConsumerRecord::value);

        JavaDStream<String> stream2 = lines.filter(line -> line.contains("Energy Consumption"));

        JavaDStream<String> eValues = stream2.flatMap(
                new FlatMapFunction<String, String>() {
                    @Override
                    public Iterator<String> call(String s) {
                        MachineProperty mp = (new Gson()).fromJson(s, MachineProperty.class);

                        return Arrays.asList(mp.value + "").iterator();
                    }
                });

        JavaDStream<String> eAverage = eValues.reduce((x, y) -> ((Float.parseFloat(x) + Float.parseFloat(y)) / 2 + ""));

        eAverage.foreachRDD(rddSend ->
                rddSend.foreach(p -> {
                    System.out.println("WERT von AVERAGE: " + p);
                    ProducerRecord<String, String> speedView
                            = new ProducerRecord<>("speedView", p);
                    _kafkaProducer.send(speedView);
                })
        );

        /************************************
         * USE CASE 2 : error notifications
         ************************************/

        kafkaInStream.foreachRDD(rdd -> {
            rdd.foreach(record -> {

                MachineProperty mp = (new Gson()).fromJson(record.value(), MachineProperty.class);
                if (mp.value == 0) {
                    System.out.println(mp.sensor + "-Sensor defekt: " + mp.machine_name + " (ID: " + mp.machine_id + ")");
                }
                switch (mp.sensor) {
                    case "Abrasion":
                        if (mp.value > 20 * 1.2) {
                            System.out.println("Wartung erforderlich: " + mp.machine_name + " (ID: " + mp.machine_id + ")");
                        }
                        break;
                    case "Amount of paint":
                        if (mp.value > 880 * 1.2) {
                            System.out.println("Wartung erforderlich: " + mp.machine_name + " (ID: " + mp.machine_id + ")");
                        }
                        break;
                    case "Brightness":
                        if (mp.value > 300 * 1.2) {
                            System.out.println("Wartung erforderlich: " + mp.machine_name + " (ID: " + mp.machine_id + ")");
                        }
                        break;
                    case "Coverage ratio":
                        if (mp.value > 90 * 1.2) {
                            System.out.println("Wartung erforderlich: " + mp.machine_name + " (ID: " + mp.machine_id + ")");
                        }
                        break;
                    case "Energy Consumption":
                        if (mp.value > 5000 * 1.2) {
                            System.out.println("Wartung erforderlich: " + mp.machine_name + " (ID: " + mp.machine_id + ")");
                        }
                        break;
                    case "Precision":
                        if (mp.value > 90 * 1.2) {
                            System.out.println("Wartung erforderlich: " + mp.machine_name + " (ID: " + mp.machine_id + ")");
                        }
                        break;
                    case "Pressure":
                        if (mp.value > 2000 * 1.2) {
                            System.out.println("Wartung erforderlich: " + mp.machine_name + " (ID: " + mp.machine_id + ")");
                        }
                        break;

                    case "Rotation Speed":
                        if (mp.value > 7200 * 1.2) {
                            System.out.println("Wartung erforderlich: " + mp.machine_name + " (ID: " + mp.machine_id + ")");
                        }
                        break;

                    case "Speed":
                        if (mp.value > 11 * 1.2) {
                            System.out.println("Wartung erforderlich: " + mp.machine_name + " (ID: " + mp.machine_id + ")");
                        }
                        break;
                    case "Temperature":
                        if (mp.value > 90 * 1.2) {
                            System.out.println("Wartung erforderlich: " + mp.machine_name + " (ID: " + mp.machine_id + ")");
                        }
                        break;
                    case "Thickness of product":
                        if (mp.value > 10 * 1.2) {
                            System.out.println("Wartung erforderlich: " + mp.machine_name + " (ID: " + mp.machine_id + ")");
                        }
                        break;

                }

            });
        });
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