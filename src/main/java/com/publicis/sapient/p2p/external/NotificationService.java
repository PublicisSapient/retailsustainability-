package com.publicis.sapient.p2p.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "notification-service")
public interface NotificationService {

    @DeleteMapping("/notification-service/notification/product/{userId}/{productId}")
    void deleteProductChat(@PathVariable("userId") String userId, @PathVariable("productId") String productId, @CookieValue("refreshToken") String refreshToken);

    @DeleteMapping("/notification-service/notification/user/{userId}")
    void deleteUserChat(@PathVariable("userId") String userId, @CookieValue("refreshToken") String refreshToken);

}
