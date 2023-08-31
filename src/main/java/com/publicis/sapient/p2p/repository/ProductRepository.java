package com.publicis.sapient.p2p.repository;

import com.publicis.sapient.p2p.entity.OfferType;
import com.publicis.sapient.p2p.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;



public interface ProductRepository extends MongoRepository<Product,String> {
    Page<Product> findByUserAndOfferTypeOrUserAndOfferType(String user1, OfferType offerType1, String user2, OfferType offerType2, Pageable pageable);

    Page<Product> findByUserAndOfferType(String user, OfferType offerType1, Pageable pageable);

    List<Product> findByUser(String user);
}
