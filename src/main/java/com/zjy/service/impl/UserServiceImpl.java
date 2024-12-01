package com.zjy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zjy.mysession.SessionKeys;
import com.zjy.pojo.User;
import com.zjy.service.UserService;
import com.zjy.mapper.UserMapper;
import com.zjy.utils.MD5Util;
import com.zjy.utils.RandomName;
import com.zjy.utils.Result;
import com.zjy.utils.ResultCodeEnum;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
* @author zjy26
* @description 针对表【z_user(用户)】的数据库操作Service实现
* @createDate 2024-11-05 23:06:01
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result regist(String uaccount, String mpassword,String phone) {
            Result result=null;
            LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getUaccount,uaccount).or().eq(User::getPhone,phone);
            boolean exists = userMapper.exists(lambdaQueryWrapper);
            if (exists){
                result=Result.build(null,ResultCodeEnum.MESSAGE_USED);
            }else{
                User user=new User();
                String s = MD5Util.encrypt(mpassword);
                user.setMpassword(s);
                user.setAvatarurl("https://s2.loli.net/2024/11/10/SAvOVmWLDThiHz8.png");
                user.setUname(RandomName.randomName(true,3));
                user.setUaccount(uaccount);
                user.setPhone(phone);
                userMapper.insert(user);
                result=Result.ok(1);
            }
        return result;
    }

    @Override
    public Result login(String uaccount, String mpassword, HttpServletRequest httpServletRequest) {
            Result result=null;
            String s = MD5Util.encrypt(mpassword);
            User user=new User();
            user.setUaccount(uaccount);
            user.setMpassword(s);
            LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getUaccount,uaccount)
                    .eq(User::getMpassword,s);
            User one = userMapper.selectOne(lambdaQueryWrapper);
            if (one==null){
                result=Result.build(null,ResultCodeEnum.LOGIN_ERROR);
            } else if (one.getUstatus()==1) {
                result=Result.build(null,ResultCodeEnum.BLACK_ACCOUNT);
            } else{
                one.setMpassword("");
                result=Result.ok(one);
                httpServletRequest.getSession().setAttribute(SessionKeys.USER_LOGINSTSTE,one);
            }
        return result;
    }

    @Override
    public User checksafe(User user) {

        Integer ustatus = user.getUstatus();
        if (ustatus==0 || ustatus==3)
        {
            user.setMpassword("");
            return user;
        }
        return null;
    }

    @Override
    public boolean myupdateById(Integer id, Integer ustatus) {
        return userMapper.changeuser(id,ustatus)>0;
    }

    @Override
    public Result updatedetail(User user) {
        int b=userMapper.updatedetail(user.getId(),user.getAvatarurl(),user.getPhone(),user.getMpassword(),user.getEmail(),user.getGender(),user.getUname());
        if (b>0){
            return Result.ok(1);
        }
        return Result.build(null,ResultCodeEnum.CDETAIL_ERROR);
    }
    /**
     * 根据标签搜索用户 - 内存查询
     * @param tagNameList 用户拥有的标签
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList) {
      if (CollectionUtils.isEmpty(tagNameList)){
          return null;
      }
      LambdaQueryWrapper<User> lambdaQueryWrapper=new LambdaQueryWrapper<>();
      List<User> userlist=userMapper.selectList(lambdaQueryWrapper);
      Gson gson=new Gson();
      userlist.stream().filter(user->{
          String usertags=user.getTags();
          if(StringUtils.isBlank(usertags)){
              return false;
          }
          Set<String> tagnameset=gson.fromJson(usertags,new TypeToken<Set<String>>(){}.getType());
          tagnameset=Optional.ofNullable(tagnameset).orElse(new HashSet<>());
          for (String s : tagNameList) {
              if(!tagnameset.contains(s)){
                  return false;
              }

          }
          return true;

      }).map(this::getSafetyUser).collect(Collectors.toList());
        //map将用户信息脱敏传给collect并集合成一个用户list列表
        return userlist;
    }
    public User getSafetyUser(User originUser)  {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUname(originUser.getUname());

        safetyUser.setUaccount(originUser.getUaccount());
        safetyUser.setAvatarurl(originUser.getAvatarurl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUstatus(originUser.getUstatus());
        safetyUser.setUpdatetime(originUser.getUpdatetime());
        safetyUser.setTags(originUser.getTags());
        safetyUser.setCreatetime(originUser.getCreatetime());
        safetyUser.setIsdeleted(originUser.getIsdeleted());
        return safetyUser;
    }



}




