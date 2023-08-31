package com.publicis.sapient.p2p.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Product", description = "Details of the Product")
public class Product {

    @Id
    @Schema(type = "String", description = "Auto-generated ID")
    private String id;

    @Schema(type = "String", description = "Name of the Product")
    @NotNull
    private String name;

    @Schema(type = "String", description = "Brief about the Product")
    private String description;

    @Schema(type = "Category", description = "Type of Product")
    private Category category;

    @Schema(type = "OfferType", description = "Type of Offer")
    @NotNull
    private OfferType offerType;

    @Schema(type = "String[]", description = "List of urls of image")
    private List<String> images;

    @Schema(type = "String", description = "Location of Product Listed")
    private String location;

    @Schema(type = "GeoLocation", description = "Location Coordinates of Product")
    private GeoLocation geoLocation;

    @Schema(type = "String", description = "User ID of Seller adding the Product")
    private String user;

    @Schema(type = "String", description = "Price Associated with the Product")
    private String price;

    @Schema(type = "Category[]", description = "Type of Products")
    private List<Category> categories;

    @Schema(type = "String", description = "Drop location Type")
    private DropLocationType dropLocationType;

    @Schema(type = "String", description = "Location of Dropping Products")
    private String dropLocation;

    @Schema(type = "Date", description = "Date when product will be dropped to chosen location")
    private Date dropDate;

    @Schema(type = "Timestamp", description = "Timestamp when the Product is created")
    private Date createdTime;

}