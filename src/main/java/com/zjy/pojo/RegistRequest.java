package com.zjy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistRequest implements Serializable{
    private String uaccount;
    private String mpassword;
    private  String phone;
}


