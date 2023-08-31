package com.publicis.sapient.p2p.external;

import com.publicis.sapient.p2p.model.UrlDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;

@FeignClient(name = "image-service")
public interface ImageService {

    @DeleteMapping("/image-service/images")
    void deleteImages(UrlDto urlDto);

    @DeleteMapping("/image-service/images/dumpImage")
    void removeImageFromDump(UrlDto urlDto);

}
