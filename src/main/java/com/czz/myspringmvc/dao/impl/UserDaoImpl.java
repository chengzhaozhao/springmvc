package com.czz.myspringmvc.dao.impl;

import com.czz.myspringmvc.annocation.Repository;
import com.czz.myspringmvc.dao.UserDao;

/**
 * @author chengzhzh@datangmobile.com
 * @create 2019-08-29 18:42
 */
@Repository("userDaoImpl")
public class UserDaoImpl implements UserDao {
    @Override
    public void insert() {
        System.out.println(" execute() UserServiceImpl.insert() start");

    }
}
