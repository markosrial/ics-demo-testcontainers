package com.ics.demotestcontainers;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaCacheRefreshConsumer {

   private static final Logger LOGGER = LoggerFactory.getLogger(KafkaCacheRefreshConsumer.class);

   @Autowired
   private ProductService productService;

   @KafkaListener(topics = "${test.cache-refresh-topic}")
   public void receiveCacheRefresh(ConsumerRecord<String, String> consumerRecord) {
      LOGGER.info("received cache refresh notification='{}'", consumerRecord.toString());
      switch (consumerRecord.value()) {
         case CachingConfig.CacheNames.OFF_SALE_PRODUCTS:
            productService.refreshOffSaleProductsCache();
            break;
         default:
            LOGGER.info("Unknown cache name");
            break;
      }
   }


}
