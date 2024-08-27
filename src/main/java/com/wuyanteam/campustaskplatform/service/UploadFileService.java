package com.wuyanteam.campustaskplatform.service;

import com.wuyanteam.campustaskplatform.entity.Task;
import com.wuyanteam.campustaskplatform.entity.UploadFile;
import com.wuyanteam.campustaskplatform.entity.User;
import com.wuyanteam.campustaskplatform.entity.photoWall;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
@Service
public interface UploadFileService {

   User uploadAvatar(String token, MultipartFile multipartFile);
   photoWall updatePhotoWall(String token, MultipartFile multipartFile);
    int deletePhotoWall(int id);
    void taskPhoto(Task task, MultipartFile multipartFile);
}