package com.publicis.sapient.p2p.controller.validator;

import com.publicis.sapient.p2p.entity.Category;
import com.publicis.sapient.p2p.exception.BusinessException;
import com.publicis.sapient.p2p.model.DonationProductDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {DonationProductValidator.class})
@ExtendWith(SpringExtension.class)
class DonationProductValidatorTest {

    @Autowired
    DonationProductValidator donationProductValidator;

    @Test
    void testValidate() {
        DonationProductDto donationProductDto = new DonationProductDto();
        donationProductDto.setName("string");
        donationProductDto.setDescription("string");
        donationProductDto.setCategories(Arrays.asList(Category.CLOTH, Category.ELECTRONIC));
        donationProductDto.setDropLocation("noida");
        donationProductDto.setDropDate(Timestamp.from(Instant.now()));

        Assertions.assertDoesNotThrow(() -> donationProductValidator.validate(donationProductDto));
    }

    @Test
    void testValidateNameNull() {
        DonationProductDto donationProductDto = new DonationProductDto();
        donationProductDto.setDescription("string");
        donationProductDto.setCategories(Arrays.asList(Category.CLOTH, Category.ELECTRONIC));
        donationProductDto.setDropLocation("noida");
        donationProductDto.setDropDate(Timestamp.from(Instant.now()));

        Assertions.assertThrows(BusinessException.class, () -> donationProductValidator.validate(donationProductDto));
    }

    @Test
    void testValidateDropLocationNull() {
        DonationProductDto donationProductDto = new DonationProductDto();
        donationProductDto.setName("string");
        donationProductDto.setDescription("string");
        donationProductDto.setCategories(Arrays.asList(Category.CLOTH, Category.ELECTRONIC));
        donationProductDto.setDropDate(Timestamp.from(Instant.now()));

        Assertions.assertThrows(BusinessException.class, () -> donationProductValidator.validate(donationProductDto));
    }

    @Test
    void testValidateDropDateNull() {
        DonationProductDto donationProductDto = new DonationProductDto();
        donationProductDto.setName("string");
        donationProductDto.setDescription("string");
        donationProductDto.setCategories(Arrays.asList(Category.CLOTH, Category.ELECTRONIC));
        donationProductDto.setDropLocation("noida");

        Assertions.assertThrows(BusinessException.class, () -> donationProductValidator.validate(donationProductDto));
    }

}