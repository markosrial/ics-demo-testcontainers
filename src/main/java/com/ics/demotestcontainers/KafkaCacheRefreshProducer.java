package com.ics.demotestcontainers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaCacheRefreshProducer {

   private static final Logger LOGGER = LoggerFactory.getLogger(KafkaCacheRefreshProducer.class);

   @Value("${test.cache-refresh-topic}")
   private String topic;

   @Autowired
   private KafkaTemplate<String, String> kafkaTemplate;

   public void refreshCacheByName(String cacheName) {
      LOGGER.info("notifying cache to refresh='{}' in topic='{}'", cacheName, topic);
      kafkaTemplate.send(topic, cacheName);
   }

}
