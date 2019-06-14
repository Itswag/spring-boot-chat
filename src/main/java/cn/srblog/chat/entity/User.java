package cn.srblog.chat.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_user")
public class User  {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String account;

    private String phone;

    private String nickname;

    private String password;

    private Boolean status;


}
