package com.zjy.controller;

import com.alibaba.druid.sql.visitor.functions.Now;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zjy.mysession.SessionKeys;
import com.zjy.pojo.*;
import com.zjy.service.UserService;
import com.zjy.utils.MD5Util;
import com.zjy.utils.Result;
import com.zjy.utils.ResultCodeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("user")
@CrossOrigin
@Tag(name = "用户管理", description = "用户管理相关的API接口")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * @param registRequest
     * @return
     */
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

    /**
     * 用户登录校验
     * @param loginRequest
     * @param httpServletRequest
     * @return
     */
    @Operation(summary = "用户登录", description = "用户登录接口，需要提供账号和密码")
    @ApiResponse(responseCode = "200", description = "登录成功", content = @Content(mediaType = "application/json"))
    @PostMapping("login")
    public Result login(@RequestBody LoginRequest loginRequest,
                        HttpServletRequest httpServletRequest) {
        String uaccount = loginRequest.getUaccount();
        String mpassword = loginRequest.getMpassword();
        String validateAccount = "^[\\w@\\$\\^!~,.\\*]{6,7}+$";
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

    /**
     * 管理员按多条件查询用户
     * @param uname
     * @param uaccount
     * @param gender
     * @param phone
     * @param email
     * @param ustatus
     * @param createtime
     * @param httpServletRequest
     * @return
     */
    @Operation(summary = "搜索用户", description = "搜索用户")
    @ApiResponse(responseCode = "200", description = "搜索成功", content = @Content(mediaType = "application/json"))
    @GetMapping("search")
    public Result search(@RequestParam(value = "uname", required = false) String uname,
                         @RequestParam(value = "uaccount", required = false) String uaccount,
                         @RequestParam(value = "gender", required = false) String gender,
                         @RequestParam(value = "phone", required = false) String phone,
                         @RequestParam(value = "email", required = false) String email,
                         @RequestParam(value = "ustatus", required = false) String ustatus,
                         @RequestParam(value = "createtime", required = false) String createtime, HttpServletRequest httpServletRequest) {
        Result result = checkgly(httpServletRequest);
        if (result.getCode() == 200) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            if (!StringUtils.isAnyBlank(uname)) {
                queryWrapper.like(User::getUname, uname);
            }  if (!StringUtils.isAnyBlank(uaccount)) {
                queryWrapper.like(User::getUaccount, uaccount);
            }   if (!StringUtils.isAnyBlank(gender)) {
                queryWrapper.eq(User::getGender, gender);
            }  if (!StringUtils.isAnyBlank(phone)) {
                queryWrapper.eq(User::getPhone, phone);
            }  if (!StringUtils.isAnyBlank(email)) {
                queryWrapper.eq(User::getEmail, email);
            }  if (!StringUtils.isAnyBlank(ustatus)) {
                queryWrapper.eq(User::getUstatus, ustatus);
            }  if (!StringUtils.isAnyBlank(createtime)) {
                queryWrapper.like(User::getCreatetime, createtime);
            }
            queryWrapper.eq(User::getIsdeleted, 0);
            List<User> list = userService.list(queryWrapper);
            result.setData(list);
        }
        return result;


    }

    /**
     * 修改用户基本信息
     * @param user
     * @param httpServletRequest
     * @return
     */
    @PostMapping("detailuser")
    public Result detailuser(@RequestBody User user,HttpServletRequest httpServletRequest){
        if(StringUtils.isAllBlank(user.getUname(),user.getEmail(),user.getAvatarurl()) && user.getGender()==null){
            return Result.build(null,ResultCodeEnum.UPDATE_ERROE);
        }
        System.out.println("user = " + user);
        Object attribute = httpServletRequest.getSession().getAttribute(SessionKeys.USER_LOGINSTSTE);
        User user1=(User) attribute;
        user.setId(user1.getId());
        user1=userService.getById(user1.getId());
        user.setUstatus(user1.getUstatus());
        user.setUpdatetime(user1.getUpdatetime());
        user.setCreatetime(user1.getCreatetime());
        user.setVersion(user1.getVersion());
        user.setIsdeleted(user1.getIsdeleted());
        user.setMpassword(user1.getMpassword());
        user.setPhone(user1.getPhone());
        if(StringUtils.isAnyBlank(user.getUname())){
            user.setUname(user1.getUname());
        }if(StringUtils.isAnyBlank(user.getAvatarurl())){

            user.setAvatarurl(user1.getAvatarurl());
            System.out.println(user.getAvatarurl());
        }if(StringUtils.isAnyBlank(user.getEmail())){
            user.setEmail(user1.getEmail());
        }if(user.getGender()==null){
            user.setGender(user1.getGender());
        }
        System.out.println("user = " + user);
        Result result = userService.updatedetail(user);
       return result;

    }

    /**
     * 管理员更改用户状态
     * @param user
     * @return
     */
     @PostMapping("changesave")
     public  Result changesave(@RequestBody DateUser user){
         System.out.println("user = " + user);
         Integer id=user.getId();
         boolean b = userService.myupdateById(id,user.getUstatus());
         if(b){
             return Result.ok(1);
         }
         return Result.build(null,ResultCodeEnum.CHANGE_REEOR);
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


    /**
     * 根据cookie检查是否为管理员
     * @param httpServletRequest
     * @return
     */
    private Result checkgly(HttpServletRequest httpServletRequest) {
        Object attribute = httpServletRequest.getSession().getAttribute(SessionKeys.USER_LOGINSTSTE);
        User user = (User) attribute;
        if (attribute == null || user.getUstatus() != SessionKeys.USER_GLY) {
            return Result.build(null, ResultCodeEnum.NO_GLY);
        }
        return Result.ok(null);
    }

    /**
     * 根据cookie获取1当前用户信息
     * @param httpServletRequest
     * @return
     */
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

    /**
     * 更改密码
     * @param password，
     * @param httpServletRequest
     * @return
     */
    @PostMapping("currentpassword")
    public  Result currentpassword(@RequestBody PasswordChange password, HttpServletRequest httpServletRequest){
        String validatePassword = "^[\\w@\\$\\^!~,.\\*]{8,16}+$";
        Object attribute = httpServletRequest.getSession().getAttribute(SessionKeys.USER_LOGINSTSTE);
        User user=(User) attribute;
        User user1=userService.getById(user.getId());
        System.out.println("user1 = " + user1);
        if(Objects.equals(password.getMpassword(), password.getMpassword1())){
            return Result.build(null,ResultCodeEnum.PASSWORD_RE);
        } else if (!Objects.equals(password.getMpassword1(), password.getMpassword2())) {
            return Result.build(null,ResultCodeEnum.PASSWORD_BYY);
        }else if(!MD5Util.encrypt(password.getMpassword()).equals(user1.getMpassword())){
            return Result.build(null,ResultCodeEnum.PASSWORD_ERROR);
        } else if (!password.getMpassword1().matches(validatePassword)) {
            return Result.build(null,ResultCodeEnum.PASSWORD_ERROR);
        }
        String p=MD5Util.encrypt(password.getMpassword1());
        user1.setMpassword(p);

        return  userService.updatedetail(user1);
    }

    /**
     * 更换关联手机号
     * @param passwordandphone
     * @param httpServletRequest
     * @return
     */
    @PostMapping("currentphone")
    public  Result currentphone(@RequestBody PasswordChange passwordandphone, HttpServletRequest httpServletRequest){
        String validatePhone = "^1[3-9]\\d{9}$";
        Object attribute = httpServletRequest.getSession().getAttribute(SessionKeys.USER_LOGINSTSTE);
        User user=(User) attribute;
        User user1=userService.getById(user.getId());
        System.out.println("user1 = " + user1);
       if(!MD5Util.encrypt(passwordandphone.getMpassword()).equals(user1.getMpassword())){
            return Result.build(null,ResultCodeEnum.PASSWORD_ERROR);
        } else if (!Objects.equals(passwordandphone.getPhone(), user1.getPhone())) {
           return Result.build(null,ResultCodeEnum.PHONE_ERRORING);
       } else if (Objects.equals(passwordandphone.getPhone(), passwordandphone.getPhone1())) {
           return Result.build(null,ResultCodeEnum.PHONE_RE);
       } else if (!passwordandphone.getPhone1().matches(validatePhone)) {
           return Result.build(null,ResultCodeEnum.PHONE_ERROR);
       }
        user1.setPhone(passwordandphone.getPhone1());
        return  userService.updatedetail(user1);
    }

    /**
     * 退出登录并清楚cookie
     * @param httpServletRequest
     * @return
     */
    @PostMapping("logout")
    public Result<Object> logout(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute(SessionKeys.USER_LOGINSTSTE);
        return Result.ok(1);
    }


}