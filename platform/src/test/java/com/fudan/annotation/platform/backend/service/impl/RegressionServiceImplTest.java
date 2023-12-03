package com.fudan.annotation.platform.backend.service.impl;

import com.fudan.annotation.platform.backend.service.RegressionService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class RegressionServiceImplTest {
    @Autowired
    RegressionService regressionService;

    @Test
    void checkoutByUser() {
//        regressionService.checkoutByUser("6167d521-9b53-4f5f-aa02-1016e67c1cfb_a31ec08e","sxz-1234ß");
    }
}