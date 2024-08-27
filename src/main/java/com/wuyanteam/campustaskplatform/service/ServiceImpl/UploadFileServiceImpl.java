package com.wuyanteam.campustaskplatform.service.ServiceImpl;

import cn.hutool.core.io.FileUtil;
import com.wuyanteam.campustaskplatform.Reposity.UserDao;
import com.wuyanteam.campustaskplatform.entity.User;
import com.wuyanteam.campustaskplatform.entity.photoWall;
import com.wuyanteam.campustaskplatform.service.UploadFileService;
import com.wuyanteam.campustaskplatform.service.UserService;
import com.wuyanteam.campustaskplatform.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UploadFileServiceImpl implements UploadFileService {

    @Resource
    private UserService userService;
    @Resource
    private UserDao userDao;

    @Value("${uploadFile.path}")
    private String path;

    @Value("${uploadFile.maxSize}")
    private long maxSize;
    @Autowired
    private com.wuyanteam.campustaskplatform.Reposity.photoWallDao photoWallDao;

    @Override
    public User uploadAvatar(String token, MultipartFile multipartFile) {
        if (multipartFile.getSize() > maxSize * Constant.MB) {
            throw new RuntimeException("超出文件上传大小限制" + maxSize + "MB");
        }
        int uploaderId = userService.InfoService(token).getId();
        //获取上传文件的主文件名与扩展名
        String primaryName = FileUtil.mainName(multipartFile.getOriginalFilename());
        String extension = FileUtil.extName(multipartFile.getOriginalFilename());
        //给上传的文件加上时间戳
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddhhmmssS");
        String nowStr = "-" + date.format(format);
        String fileName = primaryName + nowStr + "." + extension;

        try {
            File saveFile = new File(path, fileName);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            multipartFile.transferTo(saveFile);
            String avatarPath = path.substring(path.lastIndexOf("upload") - 1) + fileName;
            User user = userDao.findById(uploaderId);
            user.setAvatarPath(avatarPath);
            userDao.save(user);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public photoWall updatePhotoWall(String token, MultipartFile multipartFile) {
        if (multipartFile.getSize() > maxSize * Constant.MB) {
            throw new RuntimeException("超出文件上传大小限制" + maxSize + "MB");
        }
        int uploaderId = userService.InfoService(token).getId();
        //获取上传文件的主文件名与扩展名
        String primaryName = FileUtil.mainName(multipartFile.getOriginalFilename());
        String extension = FileUtil.extName(multipartFile.getOriginalFilename());
        //给上传的文件加上时间戳
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddhhmmssS");
        String nowStr = "-" + date.format(format);
        String fileName = primaryName + nowStr + "." + extension;

        try {
            File saveFile = new File(path, fileName);
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            multipartFile.transferTo(saveFile);
            String avatarPath = path.substring(path.lastIndexOf("upload") - 1) + fileName;
            photoWall photowall = new photoWall();
            photowall.setUserId(uploaderId);
            photowall.setPhotoPath(avatarPath);
            photoWallDao.save(photowall);
            return photowall;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int deletePhotoWall(int id) {
        if (photoWallDao.findById(id) == null) {
            return 0;
        }
        photoWallDao.deleteById(id);
        return 1;
    }
}
