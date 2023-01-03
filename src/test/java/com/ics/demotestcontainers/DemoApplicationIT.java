package com.ics.demotestcontainers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class DemoApplicationIT extends BaseIT {

   @Autowired
   @SpyBean
   private ProductRepository productRepository;

   @Autowired
   private ProductService productService;

   @Autowired
   @SpyBean
   private KafkaCacheRefreshConsumer cacheRefreshConsumer;

   @Test
   void testGetOffSaleProductsIT() throws InstanceNotFoundException, InterruptedException {

      Product hpComputer = Product.builder()
            .name("Portátil HP FQ5046NS")
            .description(
                  "Portátil ligero para trabajar o jugar.\n"
                        + "Trabaja durante todo el día gracias a un ordenador portátil HP de 15,6\" con un "
                        + "diseño ligero y una batería de larga duración. Disfruta de una visualización cómoda "
                        + "gracias a una pantalla con tecnología antiparpadeo y experimenta el rendimiento "
                        + "fiable gracias a un procesador Intel Core. Además, se ha fabricado pensando en el "
                        + "planeta, ya que cuenta con registro EPEAT Silver y la certificación ENERGY STAR.")
            .discount(0.0)
            .offSale(false)
            .price(new BigDecimal("789.95"))
            .build();

      Product msiMonitor = Product.builder()
            .name("Monitor MSI MAG275R2")
            .description(
                  "Monitor Gaming MSI Optix MAG275R2, 170 Hz, 69 cm (27\") IPS Full HD.")
            .discount(20.0)
            .offSale(true)
            .price(new BigDecimal("239.45"))
            .build();

      Product canonCamera = Product.builder()
            .name("Cámara Canon EOS 6D Mark II")
            .description(
                  "Cámara réflex Canon EOS 6D Mark II\n."
                        + "Para alcanzar nuevos objetivos creativos, necesitas contar con un rendimiento avanzado "
                        + "y unas funciones innovadoras que te ayuden a dar tus siguientes pasos en la fotografía, "
                        + "dondequiera que te lleven.")
            .discount(0.0)
            .offSale(false)
            .price(new BigDecimal("999"))
            .build();

      productRepository.saveAll(List.of(hpComputer, msiMonitor, canonCamera));

      // Check current off sale product is just the msi monitor
      List<Product> offSaleProducts = productService.getOffSaleProducts();
      assertEquals(List.of(msiMonitor), offSaleProducts);

      // Check retrieving off sale products again doesn't invoke the mongo repository
      productService.getOffSaleProducts();
      verify(productRepository, times(1)).findByOffSaleTrueOrderByDescription();

      // Promote camera product to off sales
      canonCamera = productService.promoteProduct(canonCamera.getName());
      verify(cacheRefreshConsumer, timeout(10000).times(1)).receiveCacheRefresh(any());

      // Retrieve after off sales products cache eviction
      offSaleProducts = productService.getOffSaleProducts();
      assertEquals(List.of(canonCamera, msiMonitor), offSaleProducts);

      // Verify mongo repository method is called again
      verify(productRepository, times(2)).findByOffSaleTrueOrderByDescription();
   }

}
