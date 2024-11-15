package com.zjy.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjy.mysession.SessionKeys;
import com.zjy.pojo.LoginRequest;
import com.zjy.pojo.RegistRequest;
import com.zjy.pojo.User;
import com.zjy.service.UserService;
import com.zjy.utils.Result;
import com.zjy.utils.ResultCodeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("user")
@CrossOrigin
@Tag(name = "用户管理", description = "用户管理相关的API接口")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户注册", description = "用户注册接口，需要提供账号、密码和电话号码")
    @ApiResponse(responseCode = "200", description = "注册成功", content = @Content(mediaType = "application/json"))
    @PostMapping("regist")
    public Result regist(@RequestBody RegistRequest registRequest) {
        String uaccount = registRequest.getUaccount();
        String mpassword = registRequest.getMpassword();
        String phone = registRequest.getPhone();
        String validateAccount = "^[\\w@\\$\\^!~,.\\*]{0,7}+$";
        String validatePassword = "^[\\w@\\$\\^!~,.\\*]{8,16}+$";
        String validatePhone = "^1[3-9]\\d{9}$";
        if (StringUtils.isAnyBlank(uaccount, mpassword, phone)) {
            return Result.build(null, ResultCodeEnum.MESSAGE_NUll);
        } else if (!uaccount.matches(validateAccount)) {
            return Result.build(null, ResultCodeEnum.USERACCOUNT_ERROR);
        } else if (!mpassword.matches(validatePassword)) {
            return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
        } else if (!phone.matches(validatePhone)) {
            return Result.build(null, ResultCodeEnum.PHONE_ERROR);
        } else {
            Result regist = userService.regist(uaccount, mpassword, phone);
            return regist;
        }
    }

    @Operation(summary = "用户登录", description = "用户登录接口，需要提供账号和密码")
    @ApiResponse(responseCode = "200", description = "登录成功", content = @Content(mediaType = "application/json"))
    @PostMapping("login")
    public Result login(@RequestBody LoginRequest loginRequest,
                        HttpServletRequest httpServletRequest) {
        String uaccount = loginRequest.getUaccount();
        String mpassword = loginRequest.getMpassword();
        String validateAccount = "^[\\w@\\$\\^!~,.\\*]{0,7}+$";
        String validatePassword = "^[\\w@\\$\\^!~,.\\*]{8,16}+$";
        if (StringUtils.isAnyBlank(uaccount, mpassword)) {
            return Result.build(null, ResultCodeEnum.MESSAGE_NUll);
        } else if (!uaccount.matches(validateAccount)) {
            return Result.build(null, ResultCodeEnum.USERACCOUNT_ERROR);
        } else if (!mpassword.matches(validatePassword)) {
            return Result.build(null, ResultCodeEnum.PASSWORD_ERROR);
        } else {
            return userService.login(uaccount, mpassword, httpServletRequest);
        }
    }

    @Operation(summary = "搜索用户", description = "根据用户名搜索用户")
    @ApiResponse(responseCode = "200", description = "搜索成功", content = @Content(mediaType = "application/json"))
    @GetMapping("search")
    public Result search(@Parameter(description = "用户名") String uname, HttpServletRequest httpServletRequest) {
        Result result = checkgly(httpServletRequest);
        if (result.getCode() == 200) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            if (!StringUtils.isAnyBlank(uname)) {
                queryWrapper.like(User::getUname, uname);
            }
            queryWrapper.eq(User::getIsdeleted, 0);
            List<User> list = userService.list(queryWrapper);
            result.setData(list);
        }
        return result;


    }

    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @ApiResponse(responseCode = "200", description = "删除成功", content = @Content(mediaType = "application/json"))
    @PostMapping("delete")
    public Result deleteuser(Integer id, HttpServletRequest httpServletRequest) {
        Result result = checkgly(httpServletRequest);
        if (result.getData() == null) {
            boolean b = userService.removeById(id);
            if (!b) {
                return Result.build(null, ResultCodeEnum.DELETE_ERROE);
            } else {
                return Result.ok(null);
            }
        }
        return result;
    }

    private Result checkgly(HttpServletRequest httpServletRequest) {
        Object attribute = httpServletRequest.getSession().getAttribute(SessionKeys.USER_LOGINSTSTE);
        User user = (User) attribute;
        if (attribute == null || user.getUstatus() != SessionKeys.USER_GLY) {
            return Result.build(null, ResultCodeEnum.NO_GLY);
        }
        return Result.ok(null);
    }

    @GetMapping("current")
    public Result getcurrentuser(HttpServletRequest httpServletRequest) {
        Object o = httpServletRequest.getSession().getAttribute(SessionKeys.USER_LOGINSTSTE);
        User user = (User) o;
        if (o == null) {
            return Result.build(null,ResultCodeEnum.NOTLOGIN);
        }
        Integer id = user.getId();
        User user2 = userService.getById(id);
        User user1 = userService.checksafe(user2);
        if(user1==null){
            return Result.build(null,ResultCodeEnum.BLACK_ACCOUNT);
        }
        return Result.ok(user1);

    }

    @PostMapping("logout")
    public Result<Object> logout(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute(SessionKeys.USER_LOGINSTSTE);
        return Result.ok(1);
    }


}