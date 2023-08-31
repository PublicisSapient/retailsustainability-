package com.publicis.sapient.p2p.entity;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeoLocation {

    @Schema(type = "String", description = "Latitude of the product Location")
    private String latitude;
    @Schema(type = "String", description = "Longitude of the product Location")
    private String longitude;

}