package com.tim.activiti.config;

import com.tim.activiti.util.JwtTokenHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/24.
 */

@Configuration
public class JwtConfiguration {


    @Value("${jwtKeyCloud51:jwtKeyCloud51}")
    private String key;

    @Bean
    public JwtTokenHelper jwtTokenHelper(){
        JwtTokenHelper helper = new JwtTokenHelper();
        helper.setSigningKey(key);
        helper.setVerifierKey(key);
        return helper;
    }
}
