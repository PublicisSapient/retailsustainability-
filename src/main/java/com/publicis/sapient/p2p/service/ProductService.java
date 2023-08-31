package com.publicis.sapient.p2p.service;


import com.publicis.sapient.p2p.entity.Product;
import com.publicis.sapient.p2p.model.CookieResponse;
import com.publicis.sapient.p2p.model.DonationProductDto;
import com.publicis.sapient.p2p.model.ProductDto;
import com.publicis.sapient.p2p.model.ServiceResponseDto;
import org.springframework.stereotype.Service;


@Service
public interface ProductService {

    ServiceResponseDto saveProduct(Product product, String tokenUserId);

    ServiceResponseDto deleteProduct(String productId, CookieResponse cookieResponse);

    ServiceResponseDto getUserProducts(String userId,int pageNumber, int pageSize);

    ServiceResponseDto patchProduct(String id, ProductDto productDto, String tokenUserId);

    ServiceResponseDto getProductDetails(String productId);

    void deleteAllProductByUser(String userId, String token);

    ServiceResponseDto saveDonationProduct(Product product, String tokenUserId);

    ServiceResponseDto deleteDonationProduct(String id, String tokenUserId);

    ServiceResponseDto patchDonationProduct(String id, DonationProductDto donationProductDto, String tokenUserId);

    ServiceResponseDto getUserDonationProducts(String userId, Integer pageNumber, Integer pageSize);
}