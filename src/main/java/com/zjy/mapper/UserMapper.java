package com.zjy.mapper;

import com.zjy.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author zjy26
* @description 针对表【z_user(用户)】的数据库操作Mapper
* @createDate 2024-11-05 23:06:01
* @Entity com.zjy.pojo.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

    int changeuser(Integer id, Integer ustatus);
}




