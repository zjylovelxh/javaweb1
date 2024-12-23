package com.zjy.service;

import com.zjy.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjy.utils.Result;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
* @author zjy26
* @description 针对表【z_user(用户)】的数据库操作Service
* @createDate 2024-11-05 23:06:01
*/
public interface UserService extends IService<User> {

    Result regist(String uaccount, String mpassword,String phone);

    Result login(String uaccount, String mpassword, HttpServletRequest httpServletRequest);

    User checksafe(User user);

    boolean myupdateById(Integer id, Integer ustatus);

    Result updatedetail(User user);

    List<User> searchUsersByTags(List<String> tagNameList);
}
