package com.wuyanteam.campustaskplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wuyanteam.campustaskplatform.entity.LoginDTO;
import com.wuyanteam.campustaskplatform.entity.User;
import com.wuyanteam.campustaskplatform.entity.UserDTO;
import com.wuyanteam.campustaskplatform.mapper.UserMapper;
import com.wuyanteam.campustaskplatform.service.UserService;
import com.wuyanteam.campustaskplatform.utils.JWTUtils;
import com.wuyanteam.campustaskplatform.utils.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    @PostMapping("/user")
    public Result<User> myInfo(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(userService.InfoService(token)==null){
            return Result.error("123","token已失效");
        }
        return Result.success(userService.InfoService(token));
    }
    //修改用户信息
    @PostMapping("/user/setting")
    public ResponseEntity<Object> setting(HttpServletRequest request, @RequestBody UserDTO userDTO) {

        // 验证 username 是否为空、仅包含空白字符或长度不符合要求
        String token = request.getHeader("Authorization");
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userService.InfoService(token).getId());

        // 检查参数是否为空，如果不为空则进行更新

        if (userDTO.getAge() != null) {
            updateWrapper.set("age", userDTO.getAge());
        }
        if (userDTO.getAddress() != null) {
            updateWrapper.set("address", userDTO.getAddress());
        }
        if (userDTO.getQq() != null) {
            updateWrapper.set("qq", userDTO.getQq());
        }
        if (userDTO.getEmail() != null) {
            updateWrapper.set("email", userDTO.getEmail());
        }
        if (userDTO.getPhone() != null) {
            updateWrapper.set("phone", userDTO.getPhone());
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
    public Result<User> login(String username,String password) {
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
