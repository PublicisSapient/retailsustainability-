package com.publicis.sapient.p2p.model;

import com.publicis.sapient.p2p.entity.Category;
import com.publicis.sapient.p2p.entity.GeoLocation;
import com.publicis.sapient.p2p.entity.OfferType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ProductDto", description = "Dto of the Product")
public class ProductDto {

    @Schema(type = "String", description = "Name of the Product")
    private String name;
    @Schema(type = "String", description = "Brief about the Product")
    private String description;
    @Schema(type = "Category", description = "Type of Product")
    private Category category;
    @Schema(type = "OfferType", description = "Type of Offer")
    private OfferType offerType;
    @Schema(type = "String[]", description = "List of urls of image")
    private List<String> images;
    @Schema(type = "String", description = "Location Coordinates of Product")
    private String location;
    @Schema(type = "GeoLocation", description = "Location Coordinates of Product")
    private GeoLocation geoLocation;
    @Schema(type = "String", description = "User ID of Seller adding the Product")
    private String user;
    @Schema(type = "String", description = "Price Associated with the Product")
    private String price;

}