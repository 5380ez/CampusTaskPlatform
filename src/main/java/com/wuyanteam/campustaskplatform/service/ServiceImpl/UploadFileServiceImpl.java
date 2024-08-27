package com.wuyanteam.campustaskplatform.service.ServiceImpl;

import com.wuyanteam.campustaskplatform.Reposity.TaskDao;
import com.wuyanteam.campustaskplatform.Reposity.UserDao;
import com.wuyanteam.campustaskplatform.entity.Task;
import com.wuyanteam.campustaskplatform.entity.User;
import com.wuyanteam.campustaskplatform.entity.photoWall;
import com.wuyanteam.campustaskplatform.service.UploadFileService;
import com.wuyanteam.campustaskplatform.service.UserService;
import com.wuyanteam.campustaskplatform.Reposity.photoWallDao;
import com.wuyanteam.campustaskplatform.utils.Constant;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@Service
public class UploadFileServiceImpl implements UploadFileService {
@Resource
private UserService userService;
@Resource
private UserDao userDao;
@Resource
private photoWallDao photoWallDao;
@Resource
private TaskDao taskDao;
    @Override
    public User updateAvatar(String token, MultipartFile multipartFile) {
        if (multipartFile.getSize() > 16 * Constant.MB) {
            throw new RuntimeException("超出文件上传大小限制"  + "16MB");
        }
        User user = userService.InfoService(token);
        byte[] bytes;
        try {
            bytes = multipartFile.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read file", e);
        }
        //user.setAvatar(bytes);
        userDao.save(user);
        return user;
    }

    @Override
    public photoWall updatePhotoWall(String token, MultipartFile multipartFile) {
        if (multipartFile.getSize() > 16 * Constant.MB) {
            throw new RuntimeException("超出文件上传大小限制"  + "16MB");
        }
        photoWall photowall = new photoWall();
        photowall.setUserId(userService.InfoService(token).getId());
        byte[] bytes;
        try {
            bytes = multipartFile.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read file", e);
        }
        //photowall.setPhoto(bytes);
        return photoWallDao.save(photowall);
    }

    @Override
    public int deletePhotoWall(int id) {
        if(photoWallDao.findById(id)==null){
            return 0;
        }
        photoWallDao.deleteById(id);
        return 1;
    }
    @Override
    public void taskPhoto(Task task, MultipartFile multipartFile) {
        if (multipartFile.getSize() > 16 * Constant.MB) {
            throw new RuntimeException("超出文件上传大小限制"  + "16MB");
        }
        byte[] bytes;
        try {
            bytes = multipartFile.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read file", e);
        }
        //task.setPhoto(bytes);
        taskDao.save(task);

    }
}