package com.zjy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChange implements Serializable {
    private String mpassword;
    private String mpassword1;
    private String mpassword2;
    private  String phone;
    private  String phone1;
}
