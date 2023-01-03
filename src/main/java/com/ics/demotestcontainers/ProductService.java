package com.ics.demotestcontainers;

import com.ics.demotestcontainers.CachingConfig.CacheNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

   private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

   @Autowired
   private ProductRepository productRepository;

   @Autowired
   private KafkaCacheRefreshProducer cacheRefreshProducer;

   public Product getProduct(String name) throws InstanceNotFoundException {
      return productRepository.findById(name).orElseThrow(() -> new InstanceNotFoundException());
   }

   @Cacheable(cacheNames = CacheNames.OFF_SALE_PRODUCTS)
   public List<Product> getOffSaleProducts() {
      return productRepository.findByOffSaleTrueOrderByDescription();
   }

   public Product promoteProduct(String name) throws InstanceNotFoundException {

      Product product = this.getProduct(name);
      product.setOffSale(true);

      // Update product
      productRepository.save(product);
      cacheRefreshProducer.refreshCacheByName(CacheNames.OFF_SALE_PRODUCTS);

      return product;

   }

   @CacheEvict(CacheNames.OFF_SALE_PRODUCTS)
   public void refreshOffSaleProductsCache() {
      LOGGER.info("cache '{}' eviction", CacheNames.OFF_SALE_PRODUCTS);
   }

}
