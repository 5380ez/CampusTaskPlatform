package com.wuyanteam.campustaskplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wuyanteam.campustaskplatform.entity.User;
import com.wuyanteam.campustaskplatform.mapper.UserMapper;
import com.wuyanteam.campustaskplatform.service.UserService;
import com.wuyanteam.campustaskplatform.utils.JWTUtils;
import com.wuyanteam.campustaskplatform.utils.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
@RestController
@CrossOrigin //允许该控制器跨域
//@RequestMapping("/user")
public class UserController {
    @Resource
    UserMapper userMapper;
    //用户个人信息
    @Resource
    private UserService userService;
    @PostMapping("/user/info")
    /*public List myInfo(String token)
    {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        System.out.println(userService.InfoService(token).getId());
        queryWrapper.eq("id",userService.InfoService(token).getId())
                .select("id","username","sex","age","stu_id","exp","level","like_count",
                "real_name","address","balance","take_num","publish_num","qq","email","phone");
        return userMapper.selectList(queryWrapper);
    }*/
    public Result<User> muInfo(String token){
        if(userService.InfoService(token)==null){
            return Result.error("123","token已失效");
        }
        return Result.success(userService.InfoService(token));
    }


    //修改用户信息
    @PostMapping("/user/setting")
    public ResponseEntity<Object> setting(@RequestParam int myId,
                                          @RequestParam(required = false) String username,
                                          @RequestParam(required = false) Integer age,
                                          @RequestParam(required = false) String address,
                                          @RequestParam(required = false) String qq,
                                          @RequestParam(required = false) String email,
                                          @RequestParam(required = false) String phone) {

        // 验证 username 是否为空、仅包含空白字符或长度不符合要求
        if (username != null) {
            String trimmedUsername = username.trim();
            if (trimmedUsername.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户名不能为空");
            }
            if (trimmedUsername.length() < 3 || trimmedUsername.length() > 10) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("用户名长度必须在3到10个字符之间");
            }
            username = trimmedUsername; // 使用已修剪的 username
        }

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", myId);

        // 检查参数是否为空，如果不为空则进行更新
        if (username != null) {
            updateWrapper.set("username", username);
        }
        if (age != null) {
            updateWrapper.set("age", age);
        }
        if (address != null) {
            updateWrapper.set("address", address);
        }
        if (qq != null) {
            updateWrapper.set("qq", qq);
        }
        if (email != null) {
            updateWrapper.set("email", email);
        }
        if (phone != null) {
            updateWrapper.set("phone", phone);
        }
        // 执行更新操作
        int row = userMapper.update(null, updateWrapper);
        if (row > 0) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失败");
        }
    }
    @PostMapping("/login")
    public Result<User> login(@RequestParam String username, @RequestParam String password) {
        User user=userService.LoginService(username,password);
        if(user!=null){
            String token= String.valueOf(JWTUtils.generateToken(username));
            return Result.success(user,token);
        }else{
            return Result.error("123","账号或密码错误!");
        }
    }
    //注册
    @PostMapping("/register")
    public Result<User> register(@RequestBody User newUser) {
        if(newUser!=null){
            User user=userService.RegisterService(newUser);
            if(user!=null){
                return Result.success(user,"注册成功！");
            }else{
                return Result.error("456","用户名已存在！");
            }
        }
        else{
            return Result.error("456","传入用户为空");
        }
    }
}
