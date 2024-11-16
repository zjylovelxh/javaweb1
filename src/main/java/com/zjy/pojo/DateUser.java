package com.zjy.pojo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DateUser implements Serializable {
    private Integer id;


    private String uaccount;

    private String uname;

    private String avatarurl;

    private Integer gender;

    private String mpassword;

    private String phone;

    private String email;

    private Integer ustatus;

    private String createtime;

    private String updatetime;

    private Integer version;

    private Integer isdeleted;
}
