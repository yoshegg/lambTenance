/* SimpleApp.java */

import java.util.*;

import com.google.gson.Gson;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
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

        SparkSession spark = SparkSession.builder().appName("SimpleApp").getOrCreate();
        JavaSparkContext sc = jssc.sparkContext();

        // prepare json schema
        StructField unit = DataTypes.createStructField("unit", DataTypes.StringType, true);
        StructField machine_id = DataTypes.createStructField("machine_id", DataTypes.IntegerType, true);
        StructField sensor = DataTypes.createStructField("sensor", DataTypes.StringType, true);
        StructField machine_name = DataTypes.createStructField("machine_name", DataTypes.StringType, true);
        StructField value = DataTypes.createStructField("value", DataTypes.DoubleType, true);

        List<StructField> fields = Arrays.asList(unit, machine_id, sensor, machine_name, value);
        StructType schema = DataTypes.createStructType(fields);

        //TODO: has to be computed by last offset in topic
        int numOfMessages = 7000000;

        while(true) {

            //prepare offset ranges for batch processing job
            OffsetRange[] offsetRanges2 = {
                    // topic, partition, inclusive starting offset, exclusive ending offset
                    OffsetRange.create("maintenance", 0, 0, numOfMessages)
            };

            JavaRDD<ConsumerRecord<String, String>> rddBatch = KafkaUtils.createRDD(
                    sc,
                    kafkaParams,
                    offsetRanges2,
                    LocationStrategies.PreferConsistent()
            );

            JavaRDD<String> rddBatch1 = rddBatch.flatMap(record -> Arrays.asList(record.value()).iterator());

            // Convert records of the batch RDD to Rows
            JavaRDD<Row> rowRDD = rddBatch1.map((Function<String, Row>) record -> {
                String[] attributes = record.split(",");
                return RowFactory.create(
                        attributes[0].split("\"")[3],
                        Integer.parseInt(attributes[1].trim().split("\"")[2].replace(":", "")),
                        attributes[2].trim().split("\"")[3],
                        attributes[3].trim().split("\"")[3],
                        Double.parseDouble(attributes[4].trim().split("\"")[3].replace(":", "")));
            });

            // Apply the schema to the RDD
            Dataset<Row> df = spark.createDataFrame(rowRDD, schema);
            df.show();
            df.createOrReplaceTempView("data");

            //get average value of energy consumption as String
            Dataset<Row> sqldf = spark.sql("SELECT AVG(value) FROM data WHERE sensor=\'Energy Consumption\'");
            String batchValue = (sqldf.first().getDouble(0) + "");

            //publish result on kafka topic batchview
            ProducerRecord<String, String> batchView
                    = new ProducerRecord<>("batchview", batchValue);
            _kafkaProducer.send(batchView);

            numOfMessages = numOfMessages + 10000;
            // Start the computation
            jssc.start();
            jssc.awaitTermination();
        }

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