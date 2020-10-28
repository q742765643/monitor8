package com;

import com.piesat.starter.OAPServerStartUp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName : FileTest
 * @Description :
 * @Author : zzj
 * @Date: 2020-10-28 19:24
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OAPServerStartUp.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
public class FileTest {
    @Test
    public void testCpu() throws Exception {
        System.out.println(111);
    }
}

