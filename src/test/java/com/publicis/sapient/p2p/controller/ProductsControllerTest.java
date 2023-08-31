package com.publicis.sapient.p2p.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.publicis.sapient.p2p.controller.validator.DonationProductValidator;
import com.publicis.sapient.p2p.controller.validator.ProductValidator;
import com.publicis.sapient.p2p.entity.*;
import com.publicis.sapient.p2p.model.*;
import com.publicis.sapient.p2p.service.JwtUtils;
import com.publicis.sapient.p2p.service.ProductService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {ProductsController.class})
@ExtendWith(SpringExtension.class)
class ProductsControllerTest {

    @Autowired
    ProductsController productsController;

    @MockBean
    ProductService productService;

    @MockBean
    ProductValidator productValidator;

    @MockBean
    DonationProductValidator donationProductValidator;

    @MockBean
    ModelMapper modelMapper;

    @MockBean
    JwtUtils jwtUtils;


    @Test
    void testSaveProduct() throws Exception {
        // Mock data
        ProductDto productDto = new ProductDto();
        productDto.setName("string");
        productDto.setDescription("string");
        productDto.setCategory(Category.CLOTH);
        productDto.setOfferType(OfferType.GIVEAWAY);
        productDto.setGeoLocation(new GeoLocation("12.12", "12.12"));
        productDto.setUser("1");

        Product product = new Product();
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Saved Successfully");
        serviceResponseDto.setData(product);

        doNothing().when(productValidator).validate(any());
        when(jwtUtils.getTokenFromCookie(any())).thenReturn(new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));
        when(modelMapper.map(any(ProductDto.class), any())).thenReturn(product);
        when(productService.saveProduct(any(Product.class), any())).thenReturn(serviceResponseDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "string",
                          "description": "string",
                          "category": "CLOTH",
                          "offerType": "GIVEAWAY",
                          "geoLocation": {
                            "latitude": "12.12",
                            "longitude": "12.12"
                          },
                          "user": "1"
                        }
                        """);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(productsController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        ServiceResponseDto savedProduct = new ObjectMapper().readValue(content, ServiceResponseDto.class);

        assertEquals(serviceResponseDto.getMessage(), savedProduct.getMessage());
    }

    @Test
    void testDeleteProduct() throws Exception {

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Deleted Successfully");

        doNothing().when(productValidator).validate(any());
        when(jwtUtils.getTokenFromCookie(any())).thenReturn(new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));
        when(productService.deleteProduct(any(), any())).thenReturn(serviceResponseDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/products/1")
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(productsController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        ServiceResponseDto deleteResponse = new ObjectMapper().readValue(content, ServiceResponseDto.class);

        assertEquals(deleteResponse.getMessage(), serviceResponseDto.getMessage());

    }

    @Test
    void testGetProductByAccountId() throws Exception {
        UserProductsDto userProductsDto =new UserProductsDto();

        Product product = new Product();
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("3");

        Product product2 = new Product();
        product2.setName("string2");
        product2.setDescription("string2");
        product2.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("3");



        userProductsDto.setProducts(Arrays.asList(product,product2));
        userProductsDto.setNumberOfProducts(2L);


        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Fetched Successfully");
        serviceResponseDto.setData(userProductsDto);

        doNothing().when(productValidator).validate(any());
        when(productService.getUserProducts("3", 0,24)).thenReturn(serviceResponseDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/products/3").param("pageNumber", "0").param("pageSize","24")
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(productsController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String content = response.getContentAsString();
        ServiceResponseDto productResponse = new ObjectMapper().readValue(content, new TypeReference<>() {});
        assertEquals(serviceResponseDto.getMessage(), productResponse.getMessage());

    }

    @Test
    void testGetProductByAccountId2() throws Exception {
        UserProductsDto userProductsDto =new UserProductsDto();

        Product product = new Product();
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("3");

        Product product2 = new Product();
        product2.setName("string2");
        product2.setDescription("string2");
        product2.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("3");



        userProductsDto.setProducts(Arrays.asList(product,product2));
        userProductsDto.setNumberOfProducts(2L);


        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Fetched Successfully");
        serviceResponseDto.setData(userProductsDto);

        doNothing().when(productValidator).validate(any());
        when(productService.getUserProducts("3", 0,24)).thenReturn(serviceResponseDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/products/3")
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(productsController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String content = response.getContentAsString();
        ServiceResponseDto productResponse = new ObjectMapper().readValue(content, new TypeReference<>() {});
        assertEquals(serviceResponseDto.getMessage(), productResponse.getMessage());

    }

    @Test
    void testPatchProduct() throws Exception {

        Product product = new Product();
        product.setId("1");
        product.setName("newString");
        product.setDescription("newString");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Saved Successfully");
        serviceResponseDto.setData(product);

        doNothing().when(productValidator).validate(any());
        when(jwtUtils.getTokenFromCookie(any())).thenReturn(new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));
        when(productService.patchProduct(any(), any(),any())).thenReturn(serviceResponseDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "newName",
                          "description": "newDescription"
                        }
                        """);

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(productsController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        ServiceResponseDto responseProduct = new ObjectMapper().readValue(content, ServiceResponseDto.class);
        assertEquals(responseProduct.getMessage(), serviceResponseDto.getMessage());

    }

    @Test
    void testGetProductDetails() throws Exception {
        String productId = "1";
        Product product = new Product();
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        product.setId(productId);

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Details Fetched Successfully");
        serviceResponseDto.setData(product);

        doNothing().when(productValidator).validate(any());
        when(productService.getProductDetails(productId)).thenReturn(serviceResponseDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/products/pdp/1")
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(productsController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        ServiceResponseDto responseProduct = new ObjectMapper().readValue(content, ServiceResponseDto.class);
        assertEquals(serviceResponseDto.getMessage(), responseProduct.getMessage());

    }

    @Test
    void deleteAllProductByUser() throws Exception {
        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        String userId = "1";
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("All Products Deleted");

        doNothing().when(productValidator).validate(any());
        when(jwtUtils.getTokenFromCookie(any())).thenReturn(new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));
        when(jwtUtils.getUserIdFromToken(any())).thenReturn(userId);
        doNothing().when(productService).deleteAllProductByUser(any(), any());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/products/user/1")
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(productsController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        ServiceResponseDto responseProduct = new ObjectMapper().readValue(content, ServiceResponseDto.class);
        assertEquals(serviceResponseDto.getMessage(), responseProduct.getMessage());

    }

    @Test
    void deleteAllProductByUserInvalidToken() {

        doNothing().when(productValidator).validate(any());
        when(jwtUtils.getTokenFromCookie(any())).thenReturn(new CookieResponse());
        when(jwtUtils.getUserIdFromToken(any())).thenReturn("2");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/products/user/1")
                .contentType(MediaType.APPLICATION_JSON);

        Assertions.assertThrows(ServletException.class, () -> MockMvcBuilders.standaloneSetup(productsController)
                .build()
                .perform(requestBuilder));

    }

    @Test
    void testSaveDonationProduct() throws Exception {
        // Mock data
        DonationProductDto donationProductDto = new DonationProductDto();
        donationProductDto.setName("string");
        donationProductDto.setDescription("string");
        donationProductDto.setCategories(Arrays.asList(Category.CLOTH, Category.ELECTRONIC));
        donationProductDto.setDropLocationType(DropLocationType.PUBLICIS_SAPIENT_OFFICE);
        donationProductDto.setDropLocation("noida");
        donationProductDto.setDropDate(Timestamp.from(Instant.now()));

        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategories(Arrays.asList(Category.CLOTH, Category.ELECTRONIC));
        product.setDropLocationType(DropLocationType.PUBLICIS_SAPIENT_OFFICE);
        product.setOfferType(OfferType.DONATION);
        product.setDropLocation("noida");
        product.setDropDate(Timestamp.from(Instant.now()));
        product.setUser("1");

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Saved Successfully");
        serviceResponseDto.setData(product);

        doNothing().when(donationProductValidator).validate(any());
        when(jwtUtils.getTokenFromCookie(any())).thenReturn(new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));
        when(modelMapper.map(any(DonationProductDto.class), any())).thenReturn(product);
        when(productService.saveDonationProduct(any(Product.class), any())).thenReturn(serviceResponseDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/products/donation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "string",
                          "description": "string",
                          "categories": ["CLOTH", "ELECTRONIC"],
                          "dropLocationType": "PUBLICIS_SAPIENT_OFFICE",
                          "dropLocation": "noida",
                          "dropDate": "2033-06-30"
                        }
                        """);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(productsController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        ServiceResponseDto savedProduct = new ObjectMapper().readValue(content, ServiceResponseDto.class);

        assertEquals(serviceResponseDto.getMessage(), savedProduct.getMessage());
    }

    @Test
    void testDeleteDonationProduct() throws Exception {

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Deleted Successfully");

        doNothing().when(productValidator).validate(any());
        when(jwtUtils.getTokenFromCookie(any())).thenReturn(new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));
        when(productService.deleteDonationProduct(any(), any())).thenReturn(serviceResponseDto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/products/donation/1")
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(productsController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        ServiceResponseDto deleteResponse = new ObjectMapper().readValue(content, ServiceResponseDto.class);

        assertEquals(deleteResponse.getMessage(), serviceResponseDto.getMessage());

    }

    @Test
    void testPatchDonationProduct() throws Exception {

        Product product = new Product();
        product.setId("1");
        product.setName("newString");
        product.setDescription("newString");
        product.setCategories(Arrays.asList(Category.CLOTH, Category.ELECTRONIC));
        product.setDropLocationType(DropLocationType.PUBLICIS_SAPIENT_OFFICE);
        product.setOfferType(OfferType.DONATION);
        product.setDropLocation("noida");
        product.setDropDate(Timestamp.from(Instant.now()));
        product.setUser("1");

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Saved Successfully");
        serviceResponseDto.setData(product);

        doNothing().when(productValidator).validate(any());
        when(jwtUtils.getTokenFromCookie(any())).thenReturn(new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));
        when(productService.patchDonationProduct(any(), any(),any())).thenReturn(serviceResponseDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.patch("/products/donation/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "newName",
                          "description": "newDescription"
                        }
                        """);

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(productsController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());

        String content = response.getContentAsString();
        ServiceResponseDto responseProduct = new ObjectMapper().readValue(content, ServiceResponseDto.class);
        assertEquals(responseProduct.getMessage(), serviceResponseDto.getMessage());

    }

    @Test
    void testGetUserDonationProducts() throws Exception {
        UserProductsDto userProductsDto =new UserProductsDto();

        Product product = new Product();
        product.setId("1");
        product.setName("newString");
        product.setDescription("newString");
        product.setCategories(Arrays.asList(Category.CLOTH, Category.ELECTRONIC));
        product.setDropLocationType(DropLocationType.PUBLICIS_SAPIENT_OFFICE);
        product.setOfferType(OfferType.DONATION);
        product.setDropLocation("noida");
        product.setDropDate(Timestamp.from(Instant.now()));
        product.setUser("1");

        Product product2 = new Product();
        product2.setId("2");
        product2.setName("newString");
        product2.setDescription("newString");
        product2.setCategories(Arrays.asList(Category.CLOTH, Category.ELECTRONIC));
        product2.setDropLocationType(DropLocationType.PUBLICIS_SAPIENT_OFFICE);
        product2.setOfferType(OfferType.DONATION);
        product2.setDropLocation("noida");
        product2.setDropDate(Timestamp.from(Instant.now()));
        product2.setUser("1");

        userProductsDto.setProducts(Arrays.asList(product,product2));
        userProductsDto.setNumberOfProducts(2L);

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Fetched Successfully");
        serviceResponseDto.setData(userProductsDto);

        doNothing().when(productValidator).validate(any());
        when(productService.getUserDonationProducts("1", 0,24)).thenReturn(serviceResponseDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/products/donation/1").param("pageNumber", "0").param("pageSize","24")
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(productsController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String content = response.getContentAsString();
        ServiceResponseDto productResponse = new ObjectMapper().readValue(content, new TypeReference<>() {});
        assertEquals(serviceResponseDto.getMessage(), productResponse.getMessage());

    }

    @Test
    void testGetUserDonationProductsWithoutQueryParam() throws Exception {
        UserProductsDto userProductsDto =new UserProductsDto();

        Product product = new Product();
        product.setId("1");
        product.setName("newString");
        product.setDescription("newString");
        product.setCategories(Arrays.asList(Category.CLOTH, Category.ELECTRONIC));
        product.setDropLocationType(DropLocationType.PUBLICIS_SAPIENT_OFFICE);
        product.setOfferType(OfferType.DONATION);
        product.setDropLocation("noida");
        product.setDropDate(Timestamp.from(Instant.now()));
        product.setUser("1");

        Product product2 = new Product();
        product2.setId("2");
        product2.setName("newString");
        product2.setDescription("newString");
        product2.setCategories(Arrays.asList(Category.CLOTH, Category.ELECTRONIC));
        product2.setDropLocationType(DropLocationType.PUBLICIS_SAPIENT_OFFICE);
        product2.setOfferType(OfferType.DONATION);
        product2.setDropLocation("noida");
        product2.setDropDate(Timestamp.from(Instant.now()));
        product2.setUser("1");

        userProductsDto.setProducts(Arrays.asList(product,product2));
        userProductsDto.setNumberOfProducts(2L);

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Fetched Successfully");
        serviceResponseDto.setData(userProductsDto);

        doNothing().when(productValidator).validate(any());
        when(productService.getUserDonationProducts("1", 0,24)).thenReturn(serviceResponseDto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/products/donation/1")
                .contentType(MediaType.APPLICATION_JSON);

        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(productsController)
                .build()
                .perform(requestBuilder);
        MockHttpServletResponse response = actualPerformResult.andReturn().getResponse();
        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String content = response.getContentAsString();
        ServiceResponseDto productResponse = new ObjectMapper().readValue(content, new TypeReference<>() {});
        assertEquals(serviceResponseDto.getMessage(), productResponse.getMessage());

    }

}