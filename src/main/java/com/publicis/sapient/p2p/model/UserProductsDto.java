package com.publicis.sapient.p2p.model;

import com.publicis.sapient.p2p.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "UserProductsDto", description = "Dto for the products listed by user")
public class UserProductsDto {

    @Schema(name = "List<Product>", description = "List of user Products")
    private List<Product> products;
    @Schema(name = "Long", description = "Total number of user Products")
    private Long numberOfProducts;

}
