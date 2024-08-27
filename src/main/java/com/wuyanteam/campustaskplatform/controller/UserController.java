package com.wuyanteam.campustaskplatform.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import com.wuyanteam.campustaskplatform.entity.User;
import com.wuyanteam.campustaskplatform.entity.UserDTO;
import com.wuyanteam.campustaskplatform.entity.photoWall;
import com.wuyanteam.campustaskplatform.mapper.UserMapper;
import com.wuyanteam.campustaskplatform.service.PhotoWallService;
import com.wuyanteam.campustaskplatform.service.UploadFileService;
import com.wuyanteam.campustaskplatform.service.UserService;
import com.wuyanteam.campustaskplatform.utils.JWTUtils;
import com.wuyanteam.campustaskplatform.utils.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
@RestController
@CrossOrigin //允许该控制器跨域

public class UserController {
    @Resource
    UserMapper userMapper;
    //用户个人信息
    @Resource
    private UserService userService;
    @Resource
    private UploadFileService uploadFile;
    @Resource
    PhotoWallService photoWallService;
    @PostMapping("/user/setting/changeAvatar")
    public Result<User> upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if(file.isEmpty()){
            return Result.error("上传失败，文件为空");
        }
        String token = request.getHeader("Authorization");
        User user=uploadFile.updateAvatar(token,file);
        if(user==null){
            return Result.error("头像更新失败");
        }
        return Result.success(user);
    }

    @PostMapping("/user/setting/updatePhotoWall")
    public Result<photoWall> uploadPhotoWall(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if(file.isEmpty()){
            return Result.error("上传失败，文件为空");
        }
        String token = request.getHeader("Authorization");
        photoWall photowall=uploadFile.updatePhotoWall(token,file);
        if(photowall==null){
            return Result.error("照片墙更新失败");
        }
        return Result.success(photowall);
    }

    @DeleteMapping("/user/setting/deletePhotoWall/{id}")
    public Result deletePhotoWall(@PathVariable int id) {
        if(uploadFile.deletePhotoWall(id)==1){
        return Result.success("移除成功");}
        return Result.error("该图片已被移除");
    }

    @GetMapping("/user")
    public Result<User> myInfo(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        if(userService.InfoService(token)==null){
            return Result.error("123","token已失效");
        }
        return Result.success(userService.InfoService(token));
    }
    @GetMapping("/user/photowall")
    public Result<List> myphotowall(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        return photoWallService.findPhotoWall(userService.InfoService(token).getId());
    }

    @GetMapping("/user/{id}")
    public List searchByName(@PathVariable int id)
    {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("id",id)
                .select("id","username","sex","age","stu_id","exp","level","like_count",
                        "publish_num","qq","email","phone","signature","avatarPath");
        return userMapper.selectList(queryWrapper);
    }
    @GetMapping("/user/{id}/photowall")
    public Result<List> findphotowall(@PathVariable int id) {
        return photoWallService.findPhotoWall(id);
    }

    //修改用户信息
    @PostMapping("/user/setting")
    public ResponseEntity<Object> setting(HttpServletRequest request, @RequestBody User user) {//邮箱无法修改

        // 验证 username 是否为空、仅包含空白字符或长度不符合要求
        String token = request.getHeader("Authorization");
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", userService.InfoService(token).getId());

        // 检查参数是否为空，如果不为空则进行更新

        if (user.getAge() != null) {
            updateWrapper.set("age", user.getAge());
        }
        if (user.getAddress() != null) {
            updateWrapper.set("address", user.getAddress());
        }
        if (user.getQq() != null) {
            updateWrapper.set("qq", user.getQq());
        }
        if (user.getPhone() != null) {
            updateWrapper.set("phone", user.getPhone());
        }
        if (user.getSignature() != null) {
            updateWrapper.set("signature", user.getSignature());
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
    public Result<User> login(@RequestBody User loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        User user=userService.LoginService(username,password);
        if(user!=null){
            String token= String.valueOf(JWTUtils.generateToken(username));
            return Result.success(user,token);
        }else{
            return Result.error("123","账号或密码错误!");
        }
    }

    @PostMapping("/login/findPassword")//找回密码
    public Result<User> FindPassword(@RequestBody User newUser) {//传入验证码，email，新密码
        return userService.ResetPassword(newUser);
    }

    //注册
    @PostMapping("/register")
    public Result<User> register(@RequestBody User newUser) {
        if (newUser == null) {
            return Result.error("传入用户为空");
        }
        return userService.RegisterService(newUser);
    }

}
