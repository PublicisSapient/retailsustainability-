package com.publicis.sapient.p2p.model;

import com.publicis.sapient.p2p.entity.Category;
import com.publicis.sapient.p2p.entity.DropLocationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "DonationProductDto", description = "Dto of the Donation Product")
public class DonationProductDto {

    @Schema(type = "String", description = "Name of the Product")
    private String name;

    @Schema(type = "String", description = "Brief about the Product")
    private String description;

    @Schema(type = "Category[]", description = "Type of Products")
    private List<Category> categories;

    @Schema(type = "DropLocationType", description = "Drop location Type")
    private DropLocationType dropLocationType;

    @Schema(type = "String", description = "Location of Dropping Products")
    private String dropLocation;

    @Schema(type = "Date", description = "Date when product will be dropped to chosen location")
    private Date dropDate;

}
