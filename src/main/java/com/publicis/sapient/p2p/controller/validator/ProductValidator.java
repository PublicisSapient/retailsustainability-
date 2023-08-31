package com.publicis.sapient.p2p.controller.validator;

import com.publicis.sapient.p2p.entity.GeoLocation;
import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.exception.util.ErrorCode;
import com.publicis.sapient.p2p.model.ProductDto;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

@Component
@Setter
public class ProductValidator {

    private final Logger logger = LoggerFactory.getLogger(ProductValidator.class);

    private static final String MIN_IMAGE_MESSAGE = "Validation failed. Min Number of Images can be 0";

    @Value("${images.number.restriction}")
    private String maxImage;

    public void validate(ProductDto productDto) {
        logger.info("Entering validate method inside ProductValidator");
        validateName(productDto.getName());
        validateLocation(productDto.getLocation());
        validateGeoLocation(productDto.getGeoLocation());
        validateUser(productDto.getUser());
        validateImages(productDto.getImages());
    }

    private void validateName(String name) {
        logger.info("Entering validateName method inside ProductValidator");
        if(name == null) {
            logger.error("Validation failed : Name cannot be null");
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. Name cannot be null");
        }
    }

    private void validateUser(String user) {
        logger.info("Entering validateUser method inside ProductValidator");
        if (user == null) {
            logger.error("Validation failed : User cannot be null");
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. User cannot be null");
        }
    }

    private void validateLocation(String location) {
        logger.info("Entering validateLocation method inside ProductValidator");
        if(location==null){
            logger.error("Validation failed : Location cannot be null");
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. Location cannot be null");
        }
    }

    private void validateGeoLocation(GeoLocation geoLocation) {
        logger.info("Entering validateGeoLocation method inside ProductValidator");
        if (geoLocation == null) {
            logger.error("Validation failed: GeoLocation cannot be null");
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. GeoLocation cannot be Null");
        }

        if (geoLocation.getLatitude() == null || geoLocation.getLongitude() == null || Double.parseDouble(geoLocation.getLatitude()) < -90 || Double.parseDouble(geoLocation.getLatitude()) > 90 || Double.parseDouble(geoLocation.getLongitude()) < -180 || Double.parseDouble(geoLocation.getLongitude()) > 180) {
            logger.error("Validation failed: GeoLocation latitude should be in [-90, 90] and longitude should be in [-180, 180]");
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. Location Coordinates Invalid");
        }
    }

    private void validateImages(List<String> images) {
        logger.info("Entering validateImages method inside ProductValidator");
        if(images ==  null){
            logger.error("Validation failed : Images cannot be null");
            throw new BusinessException(ErrorCode.BAD_REQUEST, MIN_IMAGE_MESSAGE);
        }

        int size = images.size();
        if(size == 0){
            logger.error("Validation failed : Min Number of Image can be 0");
            throw new BusinessException(ErrorCode.BAD_REQUEST, MIN_IMAGE_MESSAGE);
        }

        if (Integer.parseInt(maxImage) < size) {
            logger.error("Validation failed : Number Of Images Greater than Limit");
            throw new BusinessException(ErrorCode.BAD_REQUEST, MessageFormat.format("Validation failed. Max Number of Images can be {0}", maxImage));
        }
        for( String image : images)
            validateProductImage(image);
    }

    private void validateProductImage(String image) {
        logger.info("Entering validateProductImage method inside ProductValidator");
        List<String> content = new ArrayList<>();
        content.add("image/jpeg");
        content.add("image/png");
        String contentType;
        if(!image.startsWith("https://storage.googleapis.com/p2p-product-images-dev/")  || !image.matches("(?i).+\\.(jpg|jpeg|png)")){
            logger.error("Invalid File.");
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid File.");
        }
            try {
                URL url = new URL(image);
                URLConnection urlConnection = url.openConnection();
                contentType = urlConnection.getHeaderField("Content-Type");
            } catch (Exception e) {
                logger.error("Error in reading contents of file.");
                throw new BusinessException(ErrorCode.BAD_REQUEST, "Error in reading contents of file.");
            }
            if (!content.contains(contentType)) {
                logger.error("Invalid File. Please upload an image file.");
                throw new BusinessException(ErrorCode.BAD_REQUEST, "Invalid File. Please upload an image file.");
            }
        }

}
