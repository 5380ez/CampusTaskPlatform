package com.wuyanteam.campustaskplatform.service;

import com.wuyanteam.campustaskplatform.entity.User;
import com.wuyanteam.campustaskplatform.entity.photoWall;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
@Service
@Transactional
public interface UploadFileService {
    User UpdateAvatar(String token, MultipartFile multipartFile);
    photoWall updatePhotoWall(String token, MultipartFile multipartFile);
    int deletePhotoWall(int id);
}