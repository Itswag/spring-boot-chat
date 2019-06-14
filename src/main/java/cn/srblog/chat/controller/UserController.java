package cn.srblog.chat.controller;


import cn.srblog.chat.common.Result;
import cn.srblog.chat.entity.User;
import cn.srblog.chat.service.IUserService;
import cn.srblog.chat.util.QCloudSmsUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@RestController
@RequestMapping("/user/")
@Slf4j
public class UserController{

    @Autowired
    private IUserService userService;

    @PostMapping("login")
    @ResponseBody
    public Result login(String account, String password, HttpSession session){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account",account).eq("password",password);
        User user = userService.getOne(wrapper);
        if (user == null){
            return Result.createByErrorMessage("登录失败");
        }
        session.setAttribute("currentUser",user);
        return Result.createBySuccess("登录成功",user);
    }

    @PostMapping("reg")
    @ResponseBody
    public Result reg(@RequestParam("reg_account") String account, @RequestParam("reg_password") String password,
                       String nickname,String phone, String verifyCode, HttpSession session){
        String code = (String) session.getAttribute("verifyCode");
        if (code == null || !code.equals(verifyCode)){
            return Result.createByErrorMessage("验证码错误");
        }
        User user = new User();
        user.setAccount(account);
        user.setNickname(nickname);
        user.setPassword(password);
        user.setPhone(phone);
        boolean result = userService.save(user);
        if (result){
            return Result.createBySuccess("注册成功,请登录",user);
        }
        return Result.createByErrorMessage("注册失败");

    }

     @PostMapping("send")
     @ResponseBody
     public Result send(String phone, String name, HttpSession session){
         String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
         session.setAttribute("verifyCode",verifyCode);
         return QCloudSmsUtil.sendMessage(phone,name,verifyCode);
     }

    @PostMapping("checkAccount")
    @ResponseBody
    public Object checkAccount(@RequestParam("reg_account") String account){
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account",account);
        User user = userService.getOne(wrapper);
        Map<String,Object> map = new HashMap();
        if (user == null){
             map.put( "valid",true);
        }
        map.put( "valid",false);
        return map;
    }

}

