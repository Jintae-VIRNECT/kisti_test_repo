package com.virnect.uaa.global.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MaskingUtilTest {

    @Test
    void emailMasking() {
        //given
        String email = "testgmail@gmail.com";

        //when
        String maskedEmail = MaskingUtil.emailMasking(email, 4);

        //then
        assertEquals("test*****@gmai*****", maskedEmail);
    }
}