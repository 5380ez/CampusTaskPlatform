package com.wuyanteam.campustaskplatform.service.ServiceImpl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.wuyanteam.campustaskplatform.Reposity.TaskDao;
import com.wuyanteam.campustaskplatform.Reposity.UploadFileDao;
import com.wuyanteam.campustaskplatform.Reposity.UserDao;
import com.wuyanteam.campustaskplatform.entity.Task;
import com.wuyanteam.campustaskplatform.entity.UploadFile;
import com.wuyanteam.campustaskplatform.entity.User;
import com.wuyanteam.campustaskplatform.entity.photoWall;
import com.wuyanteam.campustaskplatform.service.TaskService;
import com.wuyanteam.campustaskplatform.service.UploadFileService;
import com.wuyanteam.campustaskplatform.service.UserService;
import com.wuyanteam.campustaskplatform.utils.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class UploadFileServiceImpl implements UploadFileService {

@Resource
    private UploadFileDao uploadFileDao;
@Resource
    private UserService userService;
@Resource
    private UserDao userDao;
@Resource
private TaskDao taskDao;

    @Value("${uploadFile.path}")
    private String path;

    @Value("${uploadFile.maxSize}")
    private long maxSize;
    @Autowired
    private com.wuyanteam.campustaskplatform.Reposity.photoWallDao photoWallDao;

    @Override
    public User uploadAvatar(String token, MultipartFile multipartFile) {
        //检查文件大小
        if (multipartFile.getSize() > maxSize * Constant.MB) {
            throw new RuntimeException("超出文件上传大小限制" + maxSize + "MB");
        }
        int uploaderId=userService.InfoService(token).getId();
        //获取上传文件的主文件名与扩展名
        String primaryName = FileUtil.mainName(multipartFile.getOriginalFilename());
        String extension = FileUtil.extName(multipartFile.getOriginalFilename());
        //根据文件扩展名得到文件类型
        String type = getFileType(extension);
        //给上传的文件加上时间戳
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddhhmmssS");
        String nowStr = "-" + date.format(format);
        String fileName = primaryName + nowStr + "." + extension;

        try {
            String filePath = path + type + File.separator + fileName;
            File dest = new File(filePath).getCanonicalFile();
            if (!dest.getParentFile().exists()) {
                if (ObjectUtil.isNull(dest.getParentFile().mkdirs())) {
                    throw new RuntimeException("上传文件失败：建立目录错误");
                }
            }
            multipartFile.transferTo(dest);
            if (ObjectUtil.isNull(dest)) {
                throw new RuntimeException("上传文件失败");
            }
            String avatarPath=path.substring(filePath.lastIndexOf("upload")-1)+fileName;
            System.out.println(avatarPath);
            UploadFile uploadFile = new UploadFile(null, fileName, primaryName,
                    extension, avatarPath, type, multipartFile.getSize(),
                    uploaderId, Timestamp.valueOf(LocalDateTime.now()));
            uploadFileDao.save(uploadFile);
            User user=userDao.findById(uploaderId);
            user.setAvatarPath(avatarPath);
            userDao.save(user);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public photoWall updatePhotoWall(String token, MultipartFile multipartFile){
        //检查文件大小
        if (multipartFile.getSize() > maxSize * Constant.MB) {
            throw new RuntimeException("超出文件上传大小限制" + maxSize + "MB");
        }
        int uploaderId=userService.InfoService(token).getId();
        //获取上传文件的主文件名与扩展名
        String primaryName = FileUtil.mainName(multipartFile.getOriginalFilename());
        String extension = FileUtil.extName(multipartFile.getOriginalFilename());
        //根据文件扩展名得到文件类型
        String type = getFileType(extension);
        //给上传的文件加上时间戳
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddhhmmssS");
        String nowStr = "-" + date.format(format);
        String fileName = primaryName + nowStr + "." + extension;

        try {
            String filePath = path + type + File.separator + fileName;
            File dest = new File(filePath).getCanonicalFile();
            if (!dest.getParentFile().exists()) {
                if (ObjectUtil.isNull(dest.getParentFile().mkdirs())) {
                    throw new RuntimeException("上传文件失败：建立目录错误");
                }
            }
            multipartFile.transferTo(dest);
            if (ObjectUtil.isNull(dest)) {
                throw new RuntimeException("上传文件失败");
            }
            String avatarPath=path.substring(filePath.lastIndexOf("upload")-1)+fileName;
            System.out.println(avatarPath);
            UploadFile uploadFile = new UploadFile(null, fileName, primaryName,
                    extension, avatarPath, type, multipartFile.getSize(),
                    uploaderId, Timestamp.valueOf(LocalDateTime.now()));
            uploadFileDao.save(uploadFile);
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
    public void taskPhoto(Task task, MultipartFile multipartFile){
        //检查文件大小
        if (multipartFile.getSize() > maxSize * Constant.MB) {
            throw new RuntimeException("超出文件上传大小限制" + maxSize + "MB");
        }
        //获取上传文件的主文件名与扩展名
        String primaryName = FileUtil.mainName(multipartFile.getOriginalFilename());
        String extension = FileUtil.extName(multipartFile.getOriginalFilename());
        //根据文件扩展名得到文件类型
        String type = getFileType(extension);
        //给上传的文件加上时间戳
        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMddhhmmssS");
        String nowStr = "-" + date.format(format);
        String fileName = primaryName + nowStr + "." + extension;

        try {
            String filePath = path + type + File.separator + fileName;
            File dest = new File(filePath).getCanonicalFile();
            if (!dest.getParentFile().exists()) {
                if (ObjectUtil.isNull(dest.getParentFile().mkdirs())) {
                    throw new RuntimeException("上传文件失败：建立目录错误");
                }
            }
            multipartFile.transferTo(dest);
            if (ObjectUtil.isNull(dest)) {
                throw new RuntimeException("上传文件失败");
            }
            String avatarPath=path.substring(filePath.lastIndexOf("upload")-1)+fileName;
            System.out.println(avatarPath);
            UploadFile uploadFile = new UploadFile(null, fileName, primaryName,
                    extension, avatarPath, type, multipartFile.getSize(),
                    task.getId(), Timestamp.valueOf(LocalDateTime.now()));
            uploadFileDao.save(uploadFile);
            photoWall photowall = new photoWall();
            photowall.setUserId(task.getId());
            photowall.setPhotoPath(avatarPath);
            photoWallDao.save(photowall);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public int deletePhotoWall(int id) {
        if(photoWallDao.findById(id)==null){
            return 0;
        }
        photoWallDao.deleteById(id);
        return 1;
    }

    /**
     * 根据文件扩展名给文件类型
     *
     * @param extension 文件扩展名
     * @return 文件类型
     */
    private static String getFileType(String extension) {
        String document = "txt doc pdf ppt pps xlsx xls docx csv";
        String music = "mp3 wav wma mpa ram ra aac aif m4a";
        String video = "avi mpg mpe mpeg asf wmv mov qt rm mp4 flv m4v webm ogv ogg";
        String image = "bmp dib pcp dif wmf gif jpg tif eps psd cdr iff tga pcd mpt png jpeg";
        if (image.contains(extension)) {
            return "image";
        } else if (document.contains(extension)) {
            return "document";
        } else if (music.contains(extension)) {
            return "music";
        } else if (video.contains(extension)) {
            return "video";
        } else {
            return "other";
        }
    }
}

