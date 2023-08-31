package com.publicis.sapient.p2p.service;

import com.publicis.sapient.p2p.controller.validator.DonationProductValidator;
import com.publicis.sapient.p2p.controller.validator.ProductValidator;
import com.publicis.sapient.p2p.entity.*;
import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.external.ImageService;
import com.publicis.sapient.p2p.external.IndexService;
import com.publicis.sapient.p2p.external.NotificationService;
import com.publicis.sapient.p2p.external.ProfileService;
import com.publicis.sapient.p2p.model.*;
import com.publicis.sapient.p2p.repository.ProductRepository;
import feign.FeignException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {ProductServiceImpl.class})
@ExtendWith(SpringExtension.class)
class ProductServiceImplTest {

    @Autowired
    ProductServiceImpl productService;

    @MockBean
    ProductRepository productRepository;

    @MockBean
    ModelMapper modelMapper;

    @MockBean
    ProductValidator productValidator;

    @MockBean
    DonationProductValidator donationProductValidator;

    @MockBean
    JwtUtils tokenManager;

    @MockBean
    ImageService imageService;

    @MockBean
    IndexService indexService;

    @MockBean
    ProfileService profileService;

    @MockBean
    NotificationService notificationService;

    @Test
    void testSaveProductGIVEAWAWY() {
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
        when(productRepository.save(any(Product.class))).thenReturn(product);
        ServiceResponseDto result = productService.saveProduct(product, "token");

        Assertions.assertEquals(result.getData(), serviceResponseDto.getData());

    }

    @Test
    void testSaveProductSELL() {
        Product product = new Product();
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.SELL);
        product.setPrice("100");
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Saved Successfully");
        serviceResponseDto.setData(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(tokenManager.getUserIdFromToken(any())).thenReturn("1");
        ServiceResponseDto result = productService.saveProduct(product, "token");

        Assertions.assertEquals(result.getData(), serviceResponseDto.getData());

    }

    @Test
    void testSaveProductFeignException() {
        Product product = new Product();
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");

        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(tokenManager.getUserIdFromToken(any())).thenReturn("1");
        doThrow(FeignException.class).when(indexService).addDocument(any());

        Assertions.assertThrows(BusinessException.class, () -> productService.saveProduct(product, "token"));

    }

    @Test
    void testSaveProductFeignExceptionFromImageService() {
        Product product = new Product();
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        product.setImages(Collections.singletonList("img.jpg"));

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Saved Successfully");
        serviceResponseDto.setData(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(tokenManager.getUserIdFromToken(any())).thenReturn("1");
        doThrow(FeignException.class).when(imageService).removeImageFromDump(Mockito.any(UrlDto.class));
        ServiceResponseDto result = productService.saveProduct(product, "token");

        Assertions.assertEquals(result.getData(), serviceResponseDto.getData());

    }


    @Test
    void testDeleteProduct() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setImages(Collections.singletonList("url"));
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        String token = "Bearer token";
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(tokenManager.getUserIdFromToken(token)).thenReturn(product.getUser());

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Deleted Successfully");

        ServiceResponseDto deleteDtoResponse = productService.deleteProduct(product.getId(), new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));

        Assertions.assertEquals(deleteDtoResponse,serviceResponseDto);

    }

    @Test
    void testDeleteProductFeignExceptionIndexService() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setImages(Collections.singletonList("url"));
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        String token = "Bearer token";
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(tokenManager.getUserIdFromToken(token)).thenReturn(product.getUser());
        doThrow(FeignException.class).when(indexService).deleteDocument(any());

        var cookieResponse = new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a"));
        Assertions.assertThrows(BusinessException.class, () -> productService.deleteProduct("1", cookieResponse));

    }

    @Test
    void testDeleteProductFeignExceptionImageService() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setImages(Collections.singletonList("url"));
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        String token = "Bearer token";
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(tokenManager.getUserIdFromToken(token)).thenReturn(product.getUser());
        doThrow(FeignException.class).when(imageService).deleteImages(any());

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Deleted Successfully");

        ServiceResponseDto deleteDtoResponse = productService.deleteProduct(product.getId(), new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));

        Assertions.assertEquals(deleteDtoResponse,serviceResponseDto);

    }

    @Test
    void testDeleteProductFeignExceptionNotificationService() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setImages(Collections.singletonList("url"));
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        String token = "Bearer token";
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(tokenManager.getUserIdFromToken(token)).thenReturn(product.getUser());
        doThrow(FeignException.class).when(notificationService).deleteProductChat(any(), any(), any());

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Deleted Successfully");

        ServiceResponseDto deleteDtoResponse = productService.deleteProduct(product.getId(), new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));

        Assertions.assertEquals(deleteDtoResponse,serviceResponseDto);

    }

    @Test
    void testDeleteProductImageNull() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        String token = "Bearer token";
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(tokenManager.getUserIdFromToken(token)).thenReturn(product.getUser());
        doThrow(FeignException.class).when(notificationService).deleteProductChat(any(), any(), any());

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Deleted Successfully");

        ServiceResponseDto deleteDtoResponse = productService.deleteProduct(product.getId(), new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a")));

        Assertions.assertEquals(deleteDtoResponse,serviceResponseDto);

    }

    @Test
    void testDeleteProduct2() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        var cookieResponse = new CookieResponse("2", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a"));
        Assertions.assertThrows(BusinessException.class,()->productService.deleteProduct("1", cookieResponse));
    }

    @Test
    void testDeleteProduct3() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        var cookieResponse = new CookieResponse("1", "a@a.com", "token", new Cookie("a", "a"), new Cookie("a", "a"), new Cookie("a", "a"));
        Assertions.assertThrows(BusinessException.class, () -> productService.deleteProduct("1", cookieResponse));

    }

    @Test
    void testGetProductByUser() {

        String userId = "1";
        Product product = new Product();
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser(userId);

        Product product2 = new Product();
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser(userId);

        List<Product> expectedProducts = new ArrayList<>();
        expectedProducts.add(product);
        expectedProducts.add(product2);

        Pageable pageable = PageRequest.of(0, 2);
        Page<Product> productPage = new PageImpl<>(expectedProducts, pageable, expectedProducts.size());

        UserProductsDto userProductsDto=new UserProductsDto();
        userProductsDto.setProducts(expectedProducts);
        userProductsDto.setNumberOfProducts(productPage.getTotalElements());

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Fetched Successfully");
        serviceResponseDto.setData(Arrays.asList(product,product2));


        when(productRepository.findByUserAndOfferTypeOrUserAndOfferType(userId, OfferType.GIVEAWAY, userId, OfferType.SELL, pageable)).thenReturn(productPage);

        ServiceResponseDto actualProducts = productService.getUserProducts(userId,0,2);

        Assertions.assertEquals(actualProducts.getMessage(),serviceResponseDto.getMessage());

    }

    @Test
    void testGetProductByUser2() {

        List<Product> expectedProducts = new ArrayList<>();

        Pageable pageable = PageRequest.of(0, 2);
        Page<Product> productPage = new PageImpl<>(expectedProducts, pageable, 0);

        UserProductsDto userProductsDto=new UserProductsDto();
        userProductsDto.setProducts(expectedProducts);
        userProductsDto.setNumberOfProducts(productPage.getTotalElements());

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("No Products Found");
        String userId="1";

        when(productRepository.findByUserAndOfferTypeOrUserAndOfferType(userId, OfferType.GIVEAWAY, userId, OfferType.SELL, pageable)).thenReturn(productPage);

        ServiceResponseDto actualProducts = productService.getUserProducts(userId, 0, 2);

        Assertions.assertEquals(actualProducts.getMessage(),serviceResponseDto.getMessage());

    }

    @Test
    void testPatchProduct1()  {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        product.setImages(Collections.singletonList("url"));

        Product updatedProduct = new Product();
        updatedProduct.setId("1");
        updatedProduct.setName("newName");
        updatedProduct.setDescription("string");
        updatedProduct.setCategory(Category.CLOTH);
        updatedProduct.setOfferType(OfferType.GIVEAWAY);
        updatedProduct.setGeoLocation(new GeoLocation("12.12", "12.12"));
        updatedProduct.setUser("1");
        updatedProduct.setImages(Collections.singletonList("url"));

        ProductDto productDto=new ProductDto();
        productDto.setName("newName");

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Updated Successfully");
        serviceResponseDto.setData(updatedProduct);

        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(modelMapper.map(updatedProduct,ProductDto.class)).thenReturn(new ProductDto());
        doNothing().when(productValidator).validate(any());
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);

        when(modelMapper.map(updatedProduct, ProductDto.class)).thenReturn(new ProductDto());
        doNothing().when(productValidator).validate(any());
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
        ServiceResponseDto responseProduct= productService.patchProduct("1",productDto,"1");

        Assertions.assertEquals(serviceResponseDto.getData(),responseProduct.getData());

    }

    @Test
    void testPatchProduct2()  {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");

        Product updatedProduct = new Product();
        updatedProduct.setId("1");
        updatedProduct.setName("newName");
        updatedProduct.setDescription("string");
        updatedProduct.setCategory(Category.CLOTH);
        updatedProduct.setOfferType(OfferType.GIVEAWAY);
        updatedProduct.setGeoLocation(new GeoLocation("12.12", "12.12"));
        updatedProduct.setUser("1");

        ProductDto productDto=new ProductDto();
        productDto.setName("newName");

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Updated Successfully");
        serviceResponseDto.setData(updatedProduct);

        String token = "Bearer token";
        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(modelMapper.map(updatedProduct,ProductDto.class)).thenReturn(new ProductDto());
        doNothing().when(productValidator).validate(any());
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);

        when(tokenManager.getUserIdFromToken(token)).thenReturn("1");
        when(modelMapper.map(updatedProduct, ProductDto.class)).thenReturn(new ProductDto());
        doNothing().when(productValidator).validate(any());
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
        ServiceResponseDto responseProduct= productService.patchProduct("1",productDto,"1");

        Assertions.assertEquals(serviceResponseDto.getData(),responseProduct.getData());

    }

    @Test
    void testPatchProduct2ProductNotFoundException() {
        String productId="1";
        ProductDto productDto=new ProductDto();
        productDto.setName("newName");

        String token = "Bearer token";
        when(productRepository.findById(any())).thenReturn(Optional.empty());
        when(tokenManager.getUserIdFromToken(token)).thenReturn("1");

        Assertions.assertThrows(BusinessException.class, () -> productService.patchProduct(productId,productDto,token));

    }

    @Test
    void testPatchProduct3UnauthorizedAccessException() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        product.setImages(Collections.singletonList("url"));

        Product updatedProduct = new Product();
        updatedProduct.setId("1");
        updatedProduct.setName("newName");
        updatedProduct.setDescription("string");
        updatedProduct.setCategory(Category.CLOTH);
        updatedProduct.setOfferType(OfferType.GIVEAWAY);
        updatedProduct.setGeoLocation(new GeoLocation("12.12", "12.12"));
        updatedProduct.setUser("1");
        updatedProduct.setImages(Collections.singletonList("url"));

        ProductDto productDto=new ProductDto();
        productDto.setName("newName");

        String token = "Bearer token";

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Updated Successfully");
        serviceResponseDto.setData(updatedProduct);

        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(tokenManager.getUserIdFromToken(token)).thenReturn("2");

        Assertions.assertThrows(BusinessException.class,()->productService.patchProduct("1",productDto,token));

    }

    @Test
    void testPatchProduct4FeignExceptionFromIndexService() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        product.setImages(Collections.singletonList("url"));

        Product updatedProduct = new Product();
        updatedProduct.setId("1");
        updatedProduct.setName("newName");
        updatedProduct.setDescription("string");
        updatedProduct.setCategory(Category.CLOTH);
        updatedProduct.setOfferType(OfferType.GIVEAWAY);
        updatedProduct.setGeoLocation(new GeoLocation("12.12", "12.12"));
        updatedProduct.setUser("1");
        updatedProduct.setImages(Collections.singletonList("url2"));

        ProductDto productDto=new ProductDto();
        productDto.setName("newName");

        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(modelMapper.map(updatedProduct,ProductDto.class)).thenReturn(new ProductDto());
        doNothing().when(productValidator).validate(any());
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
        doThrow(FeignException.class).when(indexService).updateDocument(any(), any());

        Assertions.assertThrows(BusinessException.class, () -> productService.patchProduct("1", productDto, "1"));

    }

    @Test
    void testPatchProduct5FeignExceptionFromImageService() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        product.setImages(Collections.singletonList("url"));

        Product updatedProduct = new Product();
        updatedProduct.setId("1");
        updatedProduct.setName("newName");
        updatedProduct.setDescription("string");
        updatedProduct.setCategory(Category.CLOTH);
        updatedProduct.setOfferType(OfferType.GIVEAWAY);
        updatedProduct.setGeoLocation(new GeoLocation("12.12", "12.12"));
        updatedProduct.setUser("1");
        updatedProduct.setImages(Collections.singletonList("url2"));

        ProductDto productDto=new ProductDto();
        productDto.setName("newName");
        productDto.setImages(Collections.singletonList("url2"));

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Updated Successfully");
        serviceResponseDto.setData(updatedProduct);

        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(modelMapper.map(updatedProduct,ProductDto.class)).thenReturn(new ProductDto());
        doNothing().when(productValidator).validate(any());
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
        doThrow(FeignException.class).when(imageService).deleteImages(any());

        ServiceResponseDto responseProduct= productService.patchProduct("1",productDto,"1");

        Assertions.assertEquals(serviceResponseDto.getData(),responseProduct.getData());

    }

    @Test
    void testPatchProduct5FeignExceptionFromImageService2() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        product.setImages(Collections.singletonList("url"));

        Product updatedProduct = new Product();
        updatedProduct.setId("1");
        updatedProduct.setName("newName");
        updatedProduct.setDescription("string");
        updatedProduct.setCategory(Category.CLOTH);
        updatedProduct.setOfferType(OfferType.GIVEAWAY);
        updatedProduct.setGeoLocation(new GeoLocation("12.12", "12.12"));
        updatedProduct.setUser("1");
        updatedProduct.setImages(Collections.singletonList("url2"));

        ProductDto productDto=new ProductDto();
        productDto.setName("newName");
        productDto.setImages(Collections.singletonList("url2"));

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Updated Successfully");
        serviceResponseDto.setData(updatedProduct);

        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(modelMapper.map(updatedProduct,ProductDto.class)).thenReturn(new ProductDto());
        doNothing().when(productValidator).validate(any());
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
        doThrow(FeignException.class).when(imageService).removeImageFromDump(Mockito.any(UrlDto.class));
        ServiceResponseDto responseProduct= productService.patchProduct("1",productDto,"1");

        Assertions.assertEquals(serviceResponseDto.getData(),responseProduct.getData());

    }

    @Test
    void testGetProductDetails() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        product.setImages(Collections.singletonList("url"));

        PublicUserDto publicUserDto=new PublicUserDto();
        publicUserDto.setId("1");
        publicUserDto.setFirstName("test");
        publicUserDto.setLastName("test");
        publicUserDto.setProfileImage("profile.jpg");
        publicUserDto.setSocialUrls(Collections.singletonList("facebook.jpg"));

        ProductDetailsDto productDetailsDto=new ProductDetailsDto();
        productDetailsDto.setProduct(product);
        productDetailsDto.setUser(publicUserDto);
        productDetailsDto.setAvgRating(4.3);
        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(200);
        serviceResponseDto.setMessage("Product Details Fetched Successfully");
        serviceResponseDto.setData(product);

        ServiceResponseDto profileServiceResponseDto=new ServiceResponseDto();
        profileServiceResponseDto.setStatusCode(200);
        profileServiceResponseDto.setMessage("User Details Fetched Successfully");
        profileServiceResponseDto.setData(product);

        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        when(profileService.getUserDetails("1")).thenReturn(profileServiceResponseDto);
        when(modelMapper.map(profileService.getUserDetails("1"),PublicUserDto.class)).thenReturn(publicUserDto);

        ServiceResponseDto responseProductDetails= productService.getProductDetails("1");

        Assertions.assertEquals(serviceResponseDto.getMessage(),responseProductDetails.getMessage());

    }

    @Test
    void testGetProductDetailsFeignException() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        product.setImages(Collections.singletonList("url"));

        PublicUserDto publicUserDto=new PublicUserDto();
        publicUserDto.setId("1");
        publicUserDto.setFirstName("test");
        publicUserDto.setLastName("test");
        publicUserDto.setProfileImage("profile.jpg");
        publicUserDto.setSocialUrls(Collections.singletonList("facebook.jpg"));

        ProductDetailsDto productDetailsDto=new ProductDetailsDto();
        productDetailsDto.setProduct(product);
        productDetailsDto.setUser(publicUserDto);
        productDetailsDto.setAvgRating(4.3);

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(200);
        serviceResponseDto.setMessage("Product Details Fetched Successfully");
        serviceResponseDto.setData(product);

        ServiceResponseDto profileServiceResponseDto=new ServiceResponseDto();
        profileServiceResponseDto.setStatusCode(200);
        profileServiceResponseDto.setMessage("User Details Fetched Successfully");
        profileServiceResponseDto.setData(product);


        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        doThrow(FeignException.class).when(profileService).getUserDetails("1");

        Assertions.assertThrows(BusinessException.class, () -> productService.getProductDetails("1"));

    }

    @Test
    void testGetProductDetailsProductNotFoundException() {
        String productId="1";
        ProductDto productDto=new ProductDto();
        productDto.setName("newName");

        when(productRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(BusinessException.class, () -> productService.getProductDetails(productId));

    }

    @Test
    void deleteAllProductByUser() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        product.setImages(Collections.singletonList("url"));

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

        when(productRepository.findByUser(any())).thenReturn(Arrays.asList(product, product2));

        Assertions.assertDoesNotThrow(() -> productService.deleteAllProductByUser("1", "token"));

    }

    @Test
    void deleteAllProductByUserFeignExceptionNotificationService() {
        Product product = new Product();
        product.setId("1");
        product.setName("string");
        product.setDescription("string");
        product.setCategory(Category.CLOTH);
        product.setOfferType(OfferType.GIVEAWAY);
        product.setGeoLocation(new GeoLocation("12.12", "12.12"));
        product.setUser("1");
        product.setImages(Collections.singletonList("url"));

        when(productRepository.findByUser(any())).thenReturn(Collections.singletonList(product));
        doThrow(FeignException.class).when(notificationService).deleteUserChat(any(), any());

        Assertions.assertDoesNotThrow(() -> productService.deleteAllProductByUser("1", "token"));

    }

    @Test
    void testSaveDonationProduct() {
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
        when(productRepository.save(any(Product.class))).thenReturn(product);
        ServiceResponseDto result = productService.saveDonationProduct(product, "token");

        Assertions.assertEquals(result.getData(), serviceResponseDto.getData());

    }

    @Test
    void testDeleteDonationProduct() {
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

        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Deleted Successfully");

        ServiceResponseDto deleteDtoResponse = productService.deleteDonationProduct(product.getId(), "1");

        Assertions.assertEquals(deleteDtoResponse,serviceResponseDto);

    }

    @Test
    void testDeleteDonationProductProductNotFound() {
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

        when(productRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(BusinessException.class, () -> productService.deleteDonationProduct("1", "1"));

    }

    @Test
    void testDeleteDonationProductInvalidUser() {
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

        when(productRepository.findById(any())).thenReturn(Optional.of(product));

        Assertions.assertThrows(BusinessException.class, () -> productService.deleteDonationProduct("1", "2"));

    }

    @Test
    void testPatchDonationProduct()  {
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

        Product updatedProduct = new Product();
        updatedProduct.setId("1");
        updatedProduct.setName("newName");
        updatedProduct.setDescription("string");
        updatedProduct.setCategories(Arrays.asList(Category.CLOTH, Category.ELECTRONIC));
        updatedProduct.setOfferType(OfferType.DONATION);
        updatedProduct.setDropLocationType(DropLocationType.PUBLICIS_SAPIENT_OFFICE);
        updatedProduct.setDropLocation("noida");
        updatedProduct.setUser("1");
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, 3050);

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dropDate = calendar.getTime();
        updatedProduct.setDropDate(dropDate);

        DonationProductDto donationProductDto=new DonationProductDto();
        donationProductDto.setName("newName");
        donationProductDto.setDropDate(dropDate);

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Updated Successfully");
        serviceResponseDto.setData(updatedProduct);

        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(modelMapper.map(any(), any())).thenReturn(new DonationProductDto());
        doNothing().when(donationProductValidator).validate(any());
        when(productRepository.save(updatedProduct)).thenReturn(updatedProduct);
        ServiceResponseDto responseProduct = productService.patchDonationProduct("1",donationProductDto,"1");

        Assertions.assertEquals(serviceResponseDto.getMessage(),responseProduct.getMessage());

    }

    @Test
    void testPatchDonationProductProductNotFound()  {

        DonationProductDto donationProductDto=new DonationProductDto();
        donationProductDto.setName("newName");

        when(productRepository.findById("1")).thenReturn(Optional.empty());

        Assertions.assertThrows(BusinessException.class, () -> productService.patchDonationProduct("1",donationProductDto,"1"));

    }

    @Test
    void testPatchDonationProductInvalidUser()  {
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

        DonationProductDto donationProductDto=new DonationProductDto();
        donationProductDto.setName("newName");

        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        Assertions.assertThrows(BusinessException.class, () -> productService.patchDonationProduct("1",donationProductDto,"2"));

    }

    @Test
    void testPatchDonationProductInvalidDropDate()  {
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

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.YEAR, 2020);

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date dropDate = calendar.getTime();

        DonationProductDto donationProductDto=new DonationProductDto();
        donationProductDto.setName("newName");
        donationProductDto.setDropDate(dropDate);

        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        Assertions.assertThrows(BusinessException.class, () -> productService.patchDonationProduct("1",donationProductDto,"1"));

    }

    @Test
    void testGetUserDonationProducts() {

        String userId = "1";
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

        List<Product> expectedProducts = new ArrayList<>();
        expectedProducts.add(product);
        expectedProducts.add(product2);

        Pageable pageable = PageRequest.of(0, 2);
        Page<Product> productPage = new PageImpl<>(expectedProducts, pageable, expectedProducts.size());

        UserProductsDto userProductsDto=new UserProductsDto();
        userProductsDto.setProducts(expectedProducts);
        userProductsDto.setNumberOfProducts(productPage.getTotalElements());

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Fetched Successfully");
        serviceResponseDto.setData(Arrays.asList(product,product2));


        when(productRepository.findByUserAndOfferType(userId, OfferType.DONATION, pageable)).thenReturn(productPage);

        ServiceResponseDto actualProducts = productService.getUserDonationProducts(userId,0,2);

        Assertions.assertEquals(actualProducts.getMessage(),serviceResponseDto.getMessage());

    }
}