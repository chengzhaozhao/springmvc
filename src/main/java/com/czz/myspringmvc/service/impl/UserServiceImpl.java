package com.czz.myspringmvc.service.impl;

import com.czz.myspringmvc.annocation.Qualifier;
import com.czz.myspringmvc.annocation.Service;
import com.czz.myspringmvc.dao.UserDao;
import com.czz.myspringmvc.service.UserService;

/**
 * @author chengzhzh@datangmobile.com
 * @create 2019-08-29 18:41
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {
    @Qualifier("userDaoImpl")
    private UserDao userDao;
    @Override
    public void insert() {
        System.out.println("UserServiceImpl.insert() start");
        userDao.insert();
        System.out.println("UserServiceImpl.insert() end");
    }
}
