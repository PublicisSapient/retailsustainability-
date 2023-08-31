package com.publicis.sapient.p2p.model;

import com.publicis.sapient.p2p.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ProductDetailDto", description = "Dto of the ProductDetail")
public class ProductDetailsDto {

    @Schema(type = "Product", description = "Details of the Product")
    private Product product;
    @Schema(type = "PublicUserDto", description = "User Details of the Product Owner")
    private PublicUserDto user;
    @Schema(type = "double", description = "Rating of the Product Owner")
    private double avgRating;

}
