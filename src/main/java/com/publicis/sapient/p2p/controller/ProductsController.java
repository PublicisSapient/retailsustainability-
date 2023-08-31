package com.publicis.sapient.p2p.controller;

import com.publicis.sapient.p2p.controller.validator.DonationProductValidator;
import com.publicis.sapient.p2p.controller.validator.ProductValidator;
import com.publicis.sapient.p2p.entity.Product;
import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.exception.util.ErrorCode;
import com.publicis.sapient.p2p.model.DonationProductDto;
import com.publicis.sapient.p2p.model.ProductDto;
import com.publicis.sapient.p2p.model.ServiceResponseDto;
import com.publicis.sapient.p2p.service.JwtUtils;
import com.publicis.sapient.p2p.service.ProductService;
import com.publicis.sapient.p2p.vo.ServiceResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/products")
public class ProductsController {

   private final Logger logger = LoggerFactory.getLogger(ProductsController.class);

   @Autowired
   private ModelMapper modelMapper;

   @Autowired
   private ProductService productService;

   @Autowired
   private ProductValidator productValidator;

   @Autowired
   private DonationProductValidator donationProductValidator;

   @Autowired
   private JwtUtils jwtUtils;

   @Operation(operationId = "saveProduct", description = "Saves the product", summary = "Saves the product to database and returns the saved object", tags = {"Products"},
           requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Product Details Dto",
                   content = @Content(schema = @Schema(implementation = ProductDto.class)), required = true),
           responses = {
                   @ApiResponse(responseCode = "200", description = "Product Saved Successfully", content = @Content(schema = @Schema(implementation = ServiceResponseDto.class))),
                   @ApiResponse(responseCode = "400", description = "Validation Failed for the Product", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
                   @ApiResponse(responseCode = "401", description = "Unauthorized Access : Fetching Products for user not logged in ", content = @Content(schema = @Schema(implementation = ServiceResponse.class)))
           })
   @PostMapping
   public ServiceResponseDto saveProduct(HttpServletRequest request, HttpServletResponse response, @RequestBody ProductDto productDto) {
      logger.info("Entering saveProduct method with endpoint: /products");
      var cookieResponse = jwtUtils.getTokenFromCookie(request);
      productValidator.validate(productDto);
      Product product = modelMapper.map(productDto, Product.class);

      response.addCookie(cookieResponse.getTokenCookie());
      response.addCookie(cookieResponse.getRefreshTokenCookie());
      response.addCookie(cookieResponse.getNormalCookie());
      return productService.saveProduct(product, cookieResponse.getUserId());
   }

   @Operation(operationId = "deleteProduct", description = "Deletes the product", summary = "Deletes the product by Id ", tags = {"Products"},
           responses = {
                   @ApiResponse(responseCode = "200", description = "Product Deleted Successfully", content = @Content(schema = @Schema(implementation = ServiceResponseDto.class))),
                   @ApiResponse(responseCode = "400", description = "Product Not Found With The Given ID", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
                   @ApiResponse(responseCode = "401", description = "Unauthorized Access : Fetching Products for user not logged in ", content = @Content(schema = @Schema(implementation = ServiceResponse.class)))
           })
   @DeleteMapping("/{productId}")
   public ServiceResponseDto deleteProduct(HttpServletRequest request, HttpServletResponse response, @PathVariable("productId") String productId){
      logger.info("Entered in deleteProduct method in controller with endpoint : /products{productId}");
      var cookieResponse = jwtUtils.getTokenFromCookie(request);
      ServiceResponseDto serviceResponseDto = productService.deleteProduct(productId, cookieResponse);

      response.addCookie(cookieResponse.getTokenCookie());
      response.addCookie(cookieResponse.getRefreshTokenCookie());
      response.addCookie(cookieResponse.getNormalCookie());
      return serviceResponseDto;
   }

   @Operation(operationId = "getUserProducts", description = "Get all the product for user", summary = " Get all the product by userId ", tags = {"Products"},
           responses = {
                   @ApiResponse(responseCode = "200", description = "All Product Fetched Successfully By UserId", content = @Content(schema = @Schema(implementation = ServiceResponseDto.class))),
                   @ApiResponse(responseCode = "401", description = "Unauthorized Access : Fetching Products for user not logged in ", content = @Content(schema = @Schema(implementation = ServiceResponse.class)))
           })
   @GetMapping("/{userId}")
   public ServiceResponseDto getUserProducts(@PathVariable("userId") String userId,@RequestParam(value="pageNumber",required = false) Integer pageNumber,@RequestParam(value = "pageSize", required = false) Integer pageSize){
      logger.info("Entered in getUserProducts method in controller with endpoint : /products/{userId}");
      pageNumber = pageNumber ==null? 0 : pageNumber;
      pageSize = pageSize ==null? 24 : pageSize;
      return productService.getUserProducts(userId,pageNumber,pageSize);
   }

   @Operation(operationId = "updateProduct", description = "Updates the product ", summary = " Updates the product stored in database and returns the updated object ", tags = {"Products"},
           requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Product details Dto ",
                   content = @Content(schema = @Schema(implementation = ProductDto.class))),
           responses = {
                   @ApiResponse(responseCode = "200", description = "Product updated Successfully", content = @Content(schema = @Schema(implementation = ServiceResponseDto.class))),
                   @ApiResponse(responseCode = "400", description = "Product Not Found With The Given ProductId ", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
                   @ApiResponse(responseCode = "401", description = "Unauthorized Access : Fetching Products for user not logged in ", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
                   @ApiResponse(responseCode = "503", description = "Exception occurred while calling index-service ", content = @Content(schema = @Schema(implementation = ServiceResponse.class)))
           })
   @PatchMapping(path="/{productId}")
   public ServiceResponseDto patchProduct(HttpServletRequest request, HttpServletResponse response, @PathVariable("productId") String productId, @RequestBody ProductDto productDto) {
      logger.info("Entered in method patchProduct in controller with endpoint : /products/{productId}");
      var cookieResponse = jwtUtils.getTokenFromCookie(request);
      ServiceResponseDto serviceResponseDto = productService.patchProduct(productId, productDto, cookieResponse.getUserId());

      response.addCookie(cookieResponse.getTokenCookie());
      response.addCookie(cookieResponse.getRefreshTokenCookie());
      response.addCookie(cookieResponse.getNormalCookie());
      return serviceResponseDto;
   }

   @Operation(operationId = "getProductDetails", description = "Get the product details", summary = " Get the product details ", tags = {"Products"},
           responses = {
                   @ApiResponse(responseCode = "200", description = "Product Details Fetched Successfully", content = @Content(schema = @Schema(implementation = ServiceResponseDto.class))),
                   @ApiResponse(responseCode = "503", description = "Exception occurred while calling profile-service  ", content = @Content(schema = @Schema(implementation = ServiceResponse.class)))
           })
   @GetMapping(path="/pdp/{productId}")
   public ServiceResponseDto getProductDetails(@PathVariable("productId") String productId) {
      logger.info("Entered in method getProductDetails in controller with endpoint : /products/pdp/{productId}");
      return productService.getProductDetails(productId);
   }

   @Operation(operationId = "deleteAllProduct", description = "Deletes all the product", summary = "Deletes all the product of User ", tags = {"Products"},
           responses = {
                   @ApiResponse(responseCode = "200", description = "AllProduct Deleted Successfully", content = @Content(schema = @Schema(implementation = ServiceResponseDto.class))),
                   @ApiResponse(responseCode = "401", description = "Unauthorized Access : Fetching Products for user not logged in ", content = @Content(schema = @Schema(implementation = ServiceResponse.class)))
           })
   @DeleteMapping("/user/{userId}")
   public ServiceResponseDto deleteAllProductByUser(HttpServletRequest request, HttpServletResponse response, @PathVariable("userId") String userId){
      logger.info("Entering deleteAllProductByUser method with endpoint: /products/user/{userId}");
      var cookieResponse = jwtUtils.getTokenFromCookie(request);
      if(!userId.equals(cookieResponse.getUserId())){
         logger.error("Unauthorized Access : Invalid Token : Deleting All Products for wrong user");
         throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid Token");
      }
      productService.deleteAllProductByUser(userId, cookieResponse.getRefreshTokenCookie().getValue());
      ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
      serviceResponseDto.setStatusCode(HttpStatus.OK.value());
      serviceResponseDto.setMessage("All Products Deleted");

      response.addCookie(cookieResponse.getTokenCookie());
      response.addCookie(cookieResponse.getRefreshTokenCookie());
      response.addCookie(cookieResponse.getNormalCookie());
      return serviceResponseDto;
   }

   @Operation(operationId = "saveDonationProduct", description = "Saves the donation product", summary = "Saves the donation product to database and returns the saved object", tags = {"Donation"},
           requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Donation Product Details Dto",
                   content = @Content(schema = @Schema(implementation = DonationProductDto.class)), required = true),
           responses = {
                   @ApiResponse(responseCode = "200", description = "Product Saved Successfully", content = @Content(schema = @Schema(implementation = ServiceResponseDto.class))),
                   @ApiResponse(responseCode = "400", description = "Validation Failed for the Product", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
                   @ApiResponse(responseCode = "403", description = "Unauthenticated : User Not Logged In", content = @Content(schema = @Schema(implementation = ServiceResponse.class)))
           })
   @PostMapping("/donation")
   public ServiceResponseDto saveDonationProduct(HttpServletRequest request, HttpServletResponse response, @RequestBody DonationProductDto donationProductDto) {
      logger.info("Entering saveDonationProduct method with endpoint: /products/donation");
      var cookieResponse = jwtUtils.getTokenFromCookie(request);
      donationProductValidator.validate(donationProductDto);
      Product product = modelMapper.map(donationProductDto, Product.class);
      ServiceResponseDto serviceResponseDto = productService.saveDonationProduct(product, cookieResponse.getUserId());

      response.addCookie(cookieResponse.getTokenCookie());
      response.addCookie(cookieResponse.getRefreshTokenCookie());
      response.addCookie(cookieResponse.getNormalCookie());
      return serviceResponseDto;
   }

   @Operation(operationId = "deleteDonationProduct", description = "Delete the donation product", summary = "Deletion the donation product from database", tags = {"Donation"},
           responses = {
                   @ApiResponse(responseCode = "200", description = "Product Saved Successfully", content = @Content(schema = @Schema(implementation = ServiceResponseDto.class))),
                   @ApiResponse(responseCode = "400", description = "Validation Failed for the Product", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
                   @ApiResponse(responseCode = "403", description = "Unauthenticated : User Not Logged In", content = @Content(schema = @Schema(implementation = ServiceResponse.class)))
           })
   @DeleteMapping("/donation/{id}")
   public ServiceResponseDto deleteDonationProduct(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id) {
      logger.info("Entering deleteDonationProduct method with endpoint: /products/donation/{id}");
      var cookieResponse = jwtUtils.getTokenFromCookie(request);
      ServiceResponseDto serviceResponseDto = productService.deleteDonationProduct(id, cookieResponse.getUserId());

      response.addCookie(cookieResponse.getTokenCookie());
      response.addCookie(cookieResponse.getRefreshTokenCookie());
      response.addCookie(cookieResponse.getNormalCookie());
      return serviceResponseDto;
   }

   @Operation(operationId = "patchDonationProduct", description = "Updates the Donation product ", summary = " Updates the donation product stored in database and returns the updated object ", tags = {"Donation"},
           requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Donation Product details Dto ",
                   content = @Content(schema = @Schema(implementation = DonationProductDto.class))),
           responses = {
                   @ApiResponse(responseCode = "200", description = "Product updated Successfully", content = @Content(schema = @Schema(implementation = ServiceResponseDto.class))),
                   @ApiResponse(responseCode = "400", description = "Product Not Found With The Given ProductId ", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
                   @ApiResponse(responseCode = "401", description = "Unauthorized Access : Updating Products for user not logged in", content = @Content(schema = @Schema(implementation = ServiceResponse.class))),
                   @ApiResponse(responseCode = "403", description = "Unauthenticated : User Not Logged In", content = @Content(schema = @Schema(implementation = ServiceResponse.class)))
           })
   @PatchMapping(path="/donation/{id}")
   public ServiceResponseDto patchDonationProduct(HttpServletRequest request, HttpServletResponse response, @PathVariable("id") String id, @RequestBody DonationProductDto donationProductDto) {
      logger.info("Entered in method patchDonationProduct in controller with endpoint : /products/donation/{id}");
      var cookieResponse = jwtUtils.getTokenFromCookie(request);
      ServiceResponseDto serviceResponseDto = productService.patchDonationProduct(id, donationProductDto, cookieResponse.getUserId());

      response.addCookie(cookieResponse.getTokenCookie());
      response.addCookie(cookieResponse.getRefreshTokenCookie());
      response.addCookie(cookieResponse.getNormalCookie());
      return serviceResponseDto;
   }

   @Operation(operationId = "getUserDonationProducts", description = "Get all the donation product for user", summary = " Get all the donation product by userId ", tags = {"Donation"},
           responses = {
                   @ApiResponse(responseCode = "200", description = "All Product Fetched Successfully By UserId", content = @Content(schema = @Schema(implementation = ServiceResponseDto.class))),
                   @ApiResponse(responseCode = "401", description = "Unauthorized Access : Fetching Products for user not logged in ", content = @Content(schema = @Schema(implementation = ServiceResponse.class)))
           })
   @GetMapping("/donation/{userId}")
   public ServiceResponseDto getUserDonationProducts(@PathVariable("userId") String userId, @RequestParam(value="pageNumber",required = false) Integer pageNumber, @RequestParam(value = "pageSize", required = false) Integer pageSize){
      logger.info("Entered in getUserProducts method in controller with endpoint : /products/donation/{userId}");
      pageNumber = pageNumber ==null? 0 : pageNumber;
      pageSize = pageSize ==null? 24 : pageSize;
      return productService.getUserDonationProducts(userId, pageNumber, pageSize);
   }

}
