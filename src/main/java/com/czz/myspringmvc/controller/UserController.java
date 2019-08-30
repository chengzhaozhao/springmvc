package com.czz.myspringmvc.controller;

import com.czz.myspringmvc.annocation.Controller;
import com.czz.myspringmvc.annocation.Qualifier;
import com.czz.myspringmvc.annocation.RequestMapping;
import com.czz.myspringmvc.service.UserService;

/**
 * @author chengzhzh@datangmobile.com
 * @create 2019-08-29 18:39
 */
@Controller("userController")
@RequestMapping("/user")
public class UserController {


    @Qualifier("userServiceImpl")
    private UserService userService;

    @RequestMapping("/insert")
    public void insert(){
        userService.insert();
    }
}
