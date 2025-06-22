package com.haiyang.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/index")
public class IndexController {

    //处理请求方法
    //方法返回数据，就是响应给前端的数据
    @RequestMapping("/test")
    public String test() {
        return "Hello SpringBoot";
    }
}
