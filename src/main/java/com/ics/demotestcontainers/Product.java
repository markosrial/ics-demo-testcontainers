package com.ics.demotestcontainers;


import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "products")
public class Product {

   @Id
   private String name;

   private String description;

   private BigDecimal price;

   private Double discount;

   private LocalDateTime insertDate;

   private boolean offSale;

}
