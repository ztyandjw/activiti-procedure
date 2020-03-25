package com.tim.activiti.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/23.
 */
@RestController
public class TestController {

    @Value("${generateUrl:fuckyou}")
    private String test;


    @Value("${disableUrl:test2}")
    private String disableUrl;

    @GetMapping("/test")
    public String test() {
        return test;
    }


    @GetMapping("/test1")
    public String test1() {
        return disableUrl;
    }
}
