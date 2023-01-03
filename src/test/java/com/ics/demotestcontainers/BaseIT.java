package com.ics.demotestcontainers;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

public abstract class BaseIT {

   private static final DockerImageName MONGO_IMAGE = DockerImageName.parse("mongo:6.0.3");
   private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:5.4.3");

   private static final MongoDBContainer mongoDBContainer;
   public static final KafkaContainer kafkaContainer;


   static {
      mongoDBContainer = new MongoDBContainer(MONGO_IMAGE);
      kafkaContainer = new KafkaContainer(KAFKA_IMAGE);
      mongoDBContainer.start();
      kafkaContainer.start();
   }

   @DynamicPropertySource
   public static void setDatasourceProperties(final DynamicPropertyRegistry registry) {
      // Sets the spring mongo uri config from his container
      registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
      // Sets the spring kafka bootstrap servers config from his container
      registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
   }

}
