package com.wyl.netty;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NettyApplicationTests {

    @Test
    void contextLoads() {

    }

    public static void main(String[] args) throws Exception{
        int read = System.in.read();
        System.out.println(read);
    }

}
