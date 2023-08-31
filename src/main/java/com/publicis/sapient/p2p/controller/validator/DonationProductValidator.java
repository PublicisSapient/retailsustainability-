package com.publicis.sapient.p2p.controller.validator;

import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.exception.util.ErrorCode;
import com.publicis.sapient.p2p.model.DonationProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DonationProductValidator {
    private final Logger logger = LoggerFactory.getLogger(DonationProductValidator.class);

    public void validate(DonationProductDto donationProductDto) {
        logger.info("Entering validate method inside DonationProductValidator");
        validateName(donationProductDto.getName());
        validateDropLocation(donationProductDto.getDropLocation());
        validateDropDate(donationProductDto.getDropDate());
    }

    private void validateName(String name) {
        logger.info("Entering validateName method inside DonationProductValidator");
        if(name == null) {
            logger.error("Validation failed : Name cannot be null");
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. Name cannot be empty");
        }
    }

    private void validateDropLocation(String dropLocation) {
        logger.info("Entering validateDropLocation method inside DonationProductValidator");
        if(dropLocation == null) {
            logger.error("Validation failed : DropLocation cannot be null");
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. DropLocation cannot be empty");
        }
    }

    private void validateDropDate(Date dropDate) {
        logger.info("Entering validateDropDate method inside DonationProductValidator");
        if(dropDate == null) {
            logger.error("Validation failed : DropDate cannot be null");
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Validation failed. DropDate cannot be empty");
        }
    }

}
