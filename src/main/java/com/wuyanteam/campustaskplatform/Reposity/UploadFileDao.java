package com.wuyanteam.campustaskplatform.Reposity;

import com.wuyanteam.campustaskplatform.entity.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadFileDao extends JpaRepository<UploadFile, Integer> {
}