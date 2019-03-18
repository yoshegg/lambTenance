# lambTenance

## Simulation
- Generates dummy output
- Provides GUI to also generate anomalies or errors of machines
- Can be run with `mvn compile exec:java`


## How to organize external dependencies
```
TOP_LEVEL_FOLDER
├── kafka_2.11-2.1.0
├── kafka-logs
├── lambTenance
└── spark-2.4.0-bin-hadoop2.7
```


## HOWTO run

1. Start Zookeeper:
```
cd kafka_2.11-2.1.0
bin/zookeeper-server-starth config/zookeeper.properties
```

2. Start Kafka:
```
cd kafka_2.11-2.1.0
bin/kafka-server-start.sh config/server.properties
```

3. Start simulated factory:
```
cd lambTenance/simulation
mvn clean compile exec:java
```

4. Compile & start speed layer:
```
cd lambTenance/sparkSpeedProject
mvn compile assembly:single
cd spark-2.4.0-bin-hadoop2.7
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre/ && ./bin/spark-submit --class "SimpleApp" --master local[4] ../lambTenance/sparkSpeedProject/target/spark-project-1.0-SNAPSHOT-jar-with-dependencies.jar
```

5. Compile & start batch layer:
```
cd lambTenance/sparkBatchProject
mvn compile assembly:single
cd spark-2.4.0-bin-hadoop2.7
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre/ && ./bin/spark-submit --class "SimpleApp" --master local[4] ../lambTenance/sparkBatchProject/target/spark-project-1.0-SNAPSHOT-jar-with-dependencies.jar
```

6. Compile & start serving (merged) layer:
```
cd lambTenance/sparkMergeProject
mvn compile assembly:single
cd spark-2.4.0-bin-hadoop2.7
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre/ && ./bin/spark-submit --class "SimpleApp" --master local[4] ../lambTenance/sparkMergeProject/target/spark-project-1.0-SNAPSHOT-jar-with-dependencies.jar
```

