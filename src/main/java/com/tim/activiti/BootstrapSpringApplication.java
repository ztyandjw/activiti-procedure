package com.tim.activiti;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/2/28.
 */

@EnableApolloConfig
@SpringBootApplication
public class BootstrapSpringApplication {
	public static void main(String[] args) {
		SpringApplication.run(BootstrapSpringApplication.class, args);
	}
}
