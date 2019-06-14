package cn.srblog.chat;

import cn.srblog.chat.entity.User;
import cn.srblog.chat.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChatApplicationTests {

    @Autowired
    private IUserService userService;
    @Test
    public void contextLoads() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account","admin").eq("password","admin");
        User user = userService.getOne(wrapper);
    }
    @Test
    public void contextLoads1() {
        User user = new User();
        user.setAccount("5552522");
        user.setNickname("测试号");
        user.setPassword("password");
        user.setPhone("13866666666");
        boolean result = userService.save(user);
    }

}
