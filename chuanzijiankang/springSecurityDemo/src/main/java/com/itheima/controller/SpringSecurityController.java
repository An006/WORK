package com.itheima.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hello")
public class SpringSecurityController {
    @RequestMapping("/add")
    @PreAuthorize("hasAuthority('add')")/*具有某种权限才能够调用该方法*/
    public String add(){
        return "success";
    }


}
