package cn.srblog.chat.service.impl;

import cn.srblog.chat.entity.User;
import cn.srblog.chat.mapper.UserMapper;
import cn.srblog.chat.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
