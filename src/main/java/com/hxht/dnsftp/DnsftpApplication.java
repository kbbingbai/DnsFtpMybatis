package com.hxht.dnsftp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.hxht.**.dao")
@SpringBootApplication
@EnableScheduling
public class DnsftpApplication {
    public static void main(String[] args) {
        SpringApplication.run(DnsftpApplication.class, args);
    }
}
