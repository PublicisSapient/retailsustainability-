package com.publicis.sapient.p2p.controller.validator;

import com.publicis.sapient.p2p.entity.Category;
import com.publicis.sapient.p2p.entity.GeoLocation;
import com.publicis.sapient.p2p.entity.OfferType;
import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.model.ProductDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;



@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {ProductValidator.class})
@ExtendWith(SpringExtension.class)
class ProductValidatorTest {

    @Autowired
    ProductValidator productValidator;

    @BeforeEach
    void beforeAll() {
        productValidator.setMaxImage("2");

    }

    @Test
    void testValidate() {
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setImages(Arrays.asList("https://storage.googleapis.com/p2p-product-images-dev/homebanner/mongodb.png"));
        productDto.setGeoLocation(new GeoLocation("12.12", "12.12"));
        productDto.setLocation("San francisco");
        productDto.setUser("1");

        Assertions.assertDoesNotThrow(() -> productValidator.validate(productDto));
    }

    @Test
    void testValidateName() {
        ProductDto productDto = new ProductDto();
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setImages(Arrays.asList("url", "url2"));
        productDto.setGeoLocation(new GeoLocation("12.12", "12.12"));
        productDto.setLocation("San francisco");
        productDto.setUser("1");

        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));
    }

    @Test
    void testValidate2() {
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setImages(Arrays.asList("https://storage.googleapis.com/p2p-product-images-dev/homebanner/mongodb.png"));
        productDto.setGeoLocation(new GeoLocation("12.12", "12.12"));
        productDto.setLocation("San francisco");
        productDto.setUser("1");

        Assertions.assertDoesNotThrow(() -> productValidator.validate(productDto));
    }

    @Test
    void testValidate3() {
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setImages(Arrays.asList("url", "url2", "url3"));
        productDto.setLocation("San francisco");
        productDto.setGeoLocation(new GeoLocation("12.12", "12.12"));
        productDto.setUser("1");

        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));
    }

    @Test
    void testValidate4() {
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setImages(Arrays.asList("url", "url2"));
        productDto.setUser("1");
        productDto.setLocation("San francisco");

        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));
    }

    @Test
    void testValidate5() {
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setImages(Arrays.asList("url", "url2"));
        productDto.setUser("1");
        productDto.setLocation("San francisco");

        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));
    }

    @Test
    void testValidate6() {
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setImages(Arrays.asList("url", "url2"));
        productDto.setLocation("San francisco");
        productDto.setGeoLocation(new GeoLocation("12.12", "12.12"));

        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));
    }

    @Test
    void testValidate7() {
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setImages(Arrays.asList("url", "url2"));
        productDto.setLocation("San francisco");
        productDto.setGeoLocation(new GeoLocation("99.12", "12.12"));

        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));
    }

    @Test
    void testValidate8() {
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setImages(Arrays.asList("url", "url2"));
        productDto.setGeoLocation(new GeoLocation("99.12", "12.12"));

        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));
    }

    @Test
    void testValidateImagesNull() {
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setLocation("San francisco");
        productDto.setUser("1");
        productDto.setGeoLocation(new GeoLocation("81.12", "12.12"));

        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));
    }

    @Test
    void testValidateImagesSizeZero() {
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setImages(new ArrayList<>());
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setLocation("San francisco");
        productDto.setUser("1");
        productDto.setGeoLocation(new GeoLocation("81.12", "12.12"));

        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));
    }

    @Test
    void testValidateProductImage() {

        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setLocation("San francisco");
        productDto.setUser("1");
        productDto.setGeoLocation(new GeoLocation("81.12", "12.12"));
        productDto.setImages(Arrays.asList("url", "url2"));

        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));

        productDto.setImages(Arrays.asList("https://storage.googleapis.com/p2p-product-images-dev/.png"));
        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));

    }

    @Test
    void testValidateGeoLocationError() {
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setImages(Arrays.asList("url", "url2"));
        productDto.setGeoLocation(new GeoLocation());
        productDto.setLocation("San francisco");
        productDto.setUser("1");

        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));
    }

    @Test
    void testValidateGeoLocationError2() {
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setImages(Arrays.asList("url", "url2"));
        GeoLocation geoLocation = new GeoLocation();
        geoLocation.setLatitude("-81");
        productDto.setGeoLocation(geoLocation);
        productDto.setLocation("San francisco");
        productDto.setUser("1");

        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));
    }

    @Test
    void testValidateGeoLocationError3() {
        failureTest("-91", "-179");
    }

    @Test
    void testValidateGeoLocationError4() {
        failureTest("-40", "-189");
    }

    @Test
    void testValidateGeoLocationError5() {
        failureTest("91", "40");
    }

    @Test
    void testValidateGeoLocationError6() {
        failureTest("1", "199");
    }

    void failureTest(String lat, String lng) {
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setImages(Arrays.asList("url", "url2"));
        productDto.setGeoLocation(new GeoLocation(lat, lng));
        productDto.setLocation("San francisco");
        productDto.setUser("1");

        Assertions.assertThrows(BusinessException.class, () -> productValidator.validate(productDto));
    }


}