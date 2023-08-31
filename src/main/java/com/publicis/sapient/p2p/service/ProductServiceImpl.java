package com.publicis.sapient.p2p.service;

import com.publicis.sapient.p2p.controller.validator.DonationProductValidator;
import com.publicis.sapient.p2p.controller.validator.ProductValidator;
import com.publicis.sapient.p2p.entity.OfferType;
import com.publicis.sapient.p2p.entity.Product;
import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.exception.util.ErrorCode;
import com.publicis.sapient.p2p.external.ImageService;
import com.publicis.sapient.p2p.external.IndexService;
import com.publicis.sapient.p2p.external.NotificationService;
import com.publicis.sapient.p2p.external.ProfileService;
import com.publicis.sapient.p2p.model.*;
import com.publicis.sapient.p2p.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.beans.FeatureDescriptor;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.*;

@Service
public class ProductServiceImpl implements ProductService{

    private final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private static final String ERR_DETAIL_NOT_FOUND_MESSAGE =  "Product not found with ID : {0}";

    private static final  String ERR_FEIGN_CALL_MESSAGE = "Exception occurred while calling index-service";

    private static final String FEIGN_EXCEPTION = "FeignException";

    private static final String IMAGE_SERVICE_EXCEPTION_MESSAGE = "Exception occurred while calling image-service : {0} : {1}";

    private static final String NOTIFICATION_SERVICE_EXCEPTION_MESSAGE = "Exception occurred while calling notification-service : {0} : {1}";

    private static final String PROFILE_SERVICE_EXCEPTION_MESSAGE = "Exception occurred while calling profile-service : {0} : {1}";

    private static final String ERR_UNAUTHORIZED_ACCESS = "Deleting for user : {0}  & Logged in user : {1}";

    private static final String DELETE_UNAUTHORIZE_MESSAGE = "Unauthorized Access : Invalid Token : Deleting Product for wrong user";


    private static  final String ERR_INVALID_DONATION_UPDATE_MESSAGE = "Drop Date : {0} can not be less than the Current Date : {1}";


    private static  final String INVALID_DONATION_UPDATE_EXCEPTION_MESSAGE = "Bad Request : Invalid Request : Drop Date : {0} can not be less than the Current Date : {1}";

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductValidator productValidator;

    @Autowired
    private DonationProductValidator donationProductValidator;

    @Autowired
    private JwtUtils tokenManager;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ImageService imageService;

    @Autowired
    private IndexService indexService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private NotificationService notificationService;

    @Override
    public ServiceResponseDto saveProduct(Product product, String tokenUserId) {
        logger.info("Entering saveProduct method inside ProductServiceImpl");

        product.setUser(tokenUserId);
        product.setCreatedTime(Timestamp.from(Instant.now()));
        product.setId(UUID.randomUUID().toString());
        if(product.getOfferType().equals(OfferType.GIVEAWAY))
            product.setPrice("0");
        try {
            logger.info("Calling index service api /index-service/index add document method");
            indexService.addDocument(product);
        } catch (Exception ex) {
            logger.error(MessageFormat.format("Not able to index product : {0} : {1} : {2} : {3}", product, ERR_FEIGN_CALL_MESSAGE, ex.getClass(), ex.getMessage()));
            throw new BusinessException(ErrorCode.SERVICE_NOT_AVAILABLE, MessageFormat.format("Not able to index product : {0}", FEIGN_EXCEPTION));
        }
        try {
            logger.info("Calling image service api /index-service/image/dumpImage");
            imageService.removeImageFromDump(new UrlDto(product.getImages()));
        } catch (Exception ex) {
            logger.error(MessageFormat.format(IMAGE_SERVICE_EXCEPTION_MESSAGE, ex.getMessage(), ex.getClass()));
            logger.error(MessageFormat.format("images not removed from dump repository : {0}", product.getImages()));
        }
        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Saved Successfully");
        serviceResponseDto.setData(productRepository.save(product));
        return serviceResponseDto;
    }

    @Override
    public ServiceResponseDto deleteProduct(String productId, CookieResponse cookieResponse) {
        logger.info("Entered in deleteProduct method in service");
        Product product=productRepository.findById(productId).orElseThrow(()-> {
            logger.error(MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, productId));
            throw new BusinessException(ErrorCode.BAD_REQUEST, MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, productId));
        });
        if(!product.getUser().equals(cookieResponse.getUserId())){
            logger.error(DELETE_UNAUTHORIZE_MESSAGE);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, MessageFormat.format(ERR_UNAUTHORIZED_ACCESS, product.getUser(), cookieResponse.getUserId()));
        }
        return deleteProductHelper(productId, product, cookieResponse.getRefreshTokenCookie().getValue());

    }

    @Override
    public ServiceResponseDto getUserProducts(String userId,int pageNumber, int pageSize) {
        logger.info("Entered in getProductByUser method in service");

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> productPage = productRepository.findByUserAndOfferTypeOrUserAndOfferType(userId, OfferType.GIVEAWAY, userId, OfferType.SELL, pageable);

        return getServiceResponseDto(productPage);

    }

    @Override
    public ServiceResponseDto patchProduct(String productId, ProductDto productDto, String tokenUserId) {
        logger.info("Entered in patchProduct method  in ProductServiceImpl");
        Product product = productRepository.findById(productId).orElseThrow(()-> {
            logger.error(MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, productId));
            throw new BusinessException(ErrorCode.BAD_REQUEST, MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, productId));
        });
        if(!product.getUser().equals(tokenUserId)){
            logger.error("Unauthorized Access : Invalid Token : Deleting All Products for wrong user");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid Token");
        }
        Product oldProduct=new Product();
        BeanUtils.copyProperties(product,oldProduct);
        BeanUtils.copyProperties(productDto,product,getNullPropertyNames(productDto));
        productValidator.validate(modelMapper.map(product, ProductDto.class));
        product.setUser(tokenUserId);
        updateProductIndex(productId,product);
        product = productRepository.save(product);

        deleteProductImages(oldProduct, product);

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Updated Successfully");
        serviceResponseDto.setData(product);
        return serviceResponseDto ;
    }

    @Override
    public ServiceResponseDto getProductDetails(String productId){
        logger.info("Entered in getProductDetails method in ProductServiceImpl");
        Product product = productRepository.findById(productId).orElseThrow(()-> {
            logger.error(MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, productId));
            throw new BusinessException(ErrorCode.BAD_REQUEST, MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, productId));
        });
        ProductDetailsDto productDetailsDto =new ProductDetailsDto();
        try {
            logger.info("Calling profile service api /profile-service/profile/{userId}");
            PublicUserDto publicUserDto= modelMapper.map(profileService.getUserDetails(product.getUser()).getData(), PublicUserDto.class);
            productDetailsDto.setUser(publicUserDto);
        } catch (Exception ex) {
            logger.error(MessageFormat.format(PROFILE_SERVICE_EXCEPTION_MESSAGE, ex.getMessage(), ex.getClass()));
            throw new BusinessException(ErrorCode.SERVICE_NOT_AVAILABLE, MessageFormat.format("Unable to fetch the user details for ProductId : {0} : {1}", productId, FEIGN_EXCEPTION));
        }
        productDetailsDto.setAvgRating(4.5); // Need to fetch from Review Service
        productDetailsDto.setProduct(product);
        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Details Fetched Successfully");
        serviceResponseDto.setData(productDetailsDto);
        return  serviceResponseDto;
    }

    @Override
    @Async
    public void deleteAllProductByUser(String userId, String token) {
        logger.info("Entered in deleteAllProductByUser method in ProductServiceImpl");
        List<Product> productList = productRepository.findByUser(userId);
        for (Product product : productList) {
            logger.atInfo().log(MessageFormat.format("Deleting the product using Async method for productId : {0}", product.getId()));
            deleteProductHelper(product.getId(), product, token);
        }

        try {
            logger.info("Calling notification service api /notification-service/notification/user/{userId} delete method");
            notificationService.deleteUserChat(userId, token);
        } catch (Exception ex) {
            logger.error(MessageFormat.format(NOTIFICATION_SERVICE_EXCEPTION_MESSAGE, ex.getMessage(), ex.getClass()));
            logger.atError().log(MessageFormat.format("chat not deleted for user : {0}", userId));
        }

    }

    @Override
    public ServiceResponseDto saveDonationProduct(Product product, String tokenUserId) {
        logger.info("Entering saveDonationProduct method inside ProductServiceImpl");
        product.setUser(tokenUserId);
        product.setCreatedTime(Timestamp.from(Instant.now()));
        product.setId(UUID.randomUUID().toString());
        product.setOfferType(OfferType.DONATION);

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Saved Successfully");
        serviceResponseDto.setData(productRepository.save(product));
        return serviceResponseDto;
    }

    @Override
    public ServiceResponseDto deleteDonationProduct(String id, String tokenUserId) {
        logger.info("Entered in deleteDonationProduct method in service");

        Product product = productRepository.findById(id).orElseThrow(()-> {
            logger.error(MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, id));
            throw new BusinessException(ErrorCode.BAD_REQUEST, MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, id));
        });

        if(!product.getUser().equals(tokenUserId)){
            logger.error(DELETE_UNAUTHORIZE_MESSAGE);
            throw new BusinessException(ErrorCode.UNAUTHORIZED, MessageFormat.format(ERR_UNAUTHORIZED_ACCESS, product.getUser(), tokenUserId));
        }

        productRepository.deleteById(id);
        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Deleted Successfully");
        return serviceResponseDto;
    }

    @Override
    public ServiceResponseDto patchDonationProduct(String id, DonationProductDto donationProductDto, String tokenUserId) {
        logger.info("Entered in patchDonationProduct method  in ProductServiceImpl");

        Product product = productRepository.findById(id).orElseThrow(()-> {
            logger.error(MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, id));
            throw new BusinessException(ErrorCode.BAD_REQUEST, MessageFormat.format(ERR_DETAIL_NOT_FOUND_MESSAGE, id));
        });

        if(!product.getUser().equals(tokenUserId)){
            logger.error("Unauthorized Access : Invalid Token : Updating Product for wrong user");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "Invalid Token");
        }

        if(donationProductDto.getDropDate().before(new Date())){
            logger.atError().log(MessageFormat.format(INVALID_DONATION_UPDATE_EXCEPTION_MESSAGE, donationProductDto.getDropDate(), new Date()));
            throw new BusinessException(ErrorCode.BAD_REQUEST, MessageFormat.format(ERR_INVALID_DONATION_UPDATE_MESSAGE, donationProductDto.getDropDate(), new Date()));
        }
        BeanUtils.copyProperties(donationProductDto, product, getNullPropertyNames(donationProductDto));
        donationProductValidator.validate(modelMapper.map(product, DonationProductDto.class));
        product.setUser(tokenUserId);
        product = productRepository.save(product);

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Updated Successfully");
        serviceResponseDto.setData(product);
        return serviceResponseDto ;
    }

    @Override
    public ServiceResponseDto getUserDonationProducts(String userId, Integer pageNumber, Integer pageSize) {
        logger.info("Entered in getProductByUser method in service");

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Product> productPage = productRepository.findByUserAndOfferType(userId, OfferType.DONATION, pageable);

        return getServiceResponseDto(productPage);
    }

    public ServiceResponseDto deleteProductHelper(String productId, Product product, String refreshToken){
        logger.info("Entered in deleteProductHelper method in ProductServiceImpl");
        if(!product.getOfferType().equals(OfferType.DONATION)) {
            deleteProductIndex(productId);

            try {
                logger.info("Calling notification service api /notification-service/notification/product/{userId}/{productId} delete method");
                notificationService.deleteProductChat(product.getUser(), productId, refreshToken);
            } catch (Exception ex) {
                logger.error(MessageFormat.format(NOTIFICATION_SERVICE_EXCEPTION_MESSAGE, ex.getMessage(), ex.getClass()));
                logger.atError().log(MessageFormat.format("chat not deleted for productId : {0}", productId));
            }
        }

        productRepository.deleteById(productId);

        if(!product.getOfferType().equals(OfferType.DONATION)) {
            deleteProductImages(product);
        }

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        serviceResponseDto.setMessage("Product Deleted Successfully");
        return serviceResponseDto;
    }

    private String[] getNullPropertyNames(Object object) {
        logger.info("Entered in getNullPropertyNames method in service");
        final BeanWrapper wrappedSource = new BeanWrapperImpl(object);
        return Arrays.stream(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(name -> wrappedSource.getPropertyValue(name) == null)
                .toArray(String[]::new);
    }

    private void updateProductIndex(String productId, Product updatedProduct) {
        logger.info("Entered in updateProductIndex method in Service");
        try {
            logger.info("Calling index service api /index-service/index/{productId} update method");
            indexService.updateDocument(productId,updatedProduct);
        } catch (Exception ex) {
            logger.error(MessageFormat.format("Not able to update product : {0} : {1} : {2} : {3}", updatedProduct, ERR_FEIGN_CALL_MESSAGE, ex.getClass(), ex.getMessage()));
            throw new BusinessException(ErrorCode.SERVICE_NOT_AVAILABLE, MessageFormat.format("Not Able to Update Product : {0}", FEIGN_EXCEPTION));
        }
    }

    private void deleteProductIndex(String productId) {
        logger.info("Entered in deleteProductIndex method in Service");
        try {
            logger.info("Calling index service api /index-service/index/{productId} delete method");
            indexService.deleteDocument(productId);
        } catch (Exception ex) {
            logger.error(MessageFormat.format("Not able to delete product : productId: {0} : {1} : {2} : {3}", productId, ERR_FEIGN_CALL_MESSAGE, ex.getClass(), ex.getMessage()));
            throw new BusinessException(ErrorCode.SERVICE_NOT_AVAILABLE, MessageFormat.format("Not Able to Delete Product : {0}", FEIGN_EXCEPTION));
        }
    }

    private void deleteProductImages(Product product) {
        logger.info("Entered in deleteProductImages method in service");
        if(product.getImages() != null) {
            var images = product.getImages();
            UrlDto urlDto = new UrlDto(images);

            try {
                logger.info("Calling image service api /image-service/images delete method");
                imageService.deleteImages(urlDto);
            } catch (Exception ex) {
                logger.error(MessageFormat.format(IMAGE_SERVICE_EXCEPTION_MESSAGE, ex.getMessage(), ex.getClass()));
                logger.atError().log(MessageFormat.format("images not deleted from cloud : {0}", images));
            }
        }
    }

    private void deleteProductImages(Product oldProduct, Product updatedProduct) {
        logger.info("Entered in deleteProductImages method in service");
        List<String> oldImages = oldProduct.getImages() != null ? oldProduct.getImages() : new ArrayList<>();
        List<String> updatedImages = updatedProduct.getImages() != null ? updatedProduct.getImages() : new ArrayList<>();


        List<String> listImagesToDelete = oldImages.stream().filter(image -> !updatedImages.contains(image)).toList();
        List<String> listImagesToRemoveFromDump = updatedImages.stream().filter(image -> !oldImages.contains(image)).toList();
        if(!listImagesToDelete.isEmpty()) {
            UrlDto urlDto = new UrlDto(listImagesToDelete);
            try {
                logger.info("Calling image service api /image-service/images delete method");
                imageService.deleteImages(urlDto);
            } catch (Exception ex) {
                logger.error(MessageFormat.format(IMAGE_SERVICE_EXCEPTION_MESSAGE, ex.getMessage(), ex.getClass()));
                logger.atError().log(MessageFormat.format("images not deleted from cloud : {0}", listImagesToDelete));
            }
        }
        if(!listImagesToRemoveFromDump.isEmpty()) {
            try {
                logger.info("Calling image service api /index-service/image/dumpImage");
                imageService.removeImageFromDump(new UrlDto(listImagesToRemoveFromDump));
            } catch (Exception ex) {
                logger.error(MessageFormat.format(IMAGE_SERVICE_EXCEPTION_MESSAGE, ex.getMessage(), ex.getClass()));
                logger.atError().log(MessageFormat.format("images not removed from dump repository : {0}", listImagesToRemoveFromDump));
            }
        }
    }

    private static ServiceResponseDto getServiceResponseDto(Page<Product> productPage) {
        List<Product> productList = productPage.getContent();

        UserProductsDto userProductsDto=new UserProductsDto();
        userProductsDto.setProducts(productList);
        userProductsDto.setNumberOfProducts(productPage.getTotalElements());

        ServiceResponseDto serviceResponseDto=new ServiceResponseDto();
        serviceResponseDto.setStatusCode(HttpStatus.OK.value());
        String message = productList.isEmpty() ? "No Products Found" : "Product Fetched Successfully";
        serviceResponseDto.setMessage(message);
        serviceResponseDto.setData(userProductsDto);
        return serviceResponseDto;
    }

}
