package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * @TableName z_user
 */
@TableName(value ="z_user")
@Data
public class User implements Serializable {
    @TableId
    private Integer id;

    private String uaccount;

    private String uname;

    private String avatarurl;

    private Integer gender;

    private String mpassword;

    private String phone;

    private String email;

    private Integer ustatus;

    private Date createtime;


    private Date updatetime;
    private  String tags;

    @Version
    private Integer version;

    @TableLogic
    private Integer isdeleted;

    private static final long serialVersionUID = 1L;
}