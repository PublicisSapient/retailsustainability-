package com.publicis.sapient.p2p.external;

import com.publicis.sapient.p2p.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "index-service")
public interface IndexService {

    @PostMapping("/index-service/index")
    void addDocument(Product product);

    @DeleteMapping("/index-service/index/{productId}")
    void deleteDocument(@PathVariable String productId);

    @PutMapping("/index-service/index/{productId}")
    void updateDocument(@PathVariable String productId, Product product);
}
