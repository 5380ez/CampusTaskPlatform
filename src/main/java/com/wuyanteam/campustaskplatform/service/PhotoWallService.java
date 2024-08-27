package com.wuyanteam.campustaskplatform.service;

import com.github.yulichang.extension.mapping.base.MPJDeepService;
import com.wuyanteam.campustaskplatform.entity.photoWall;
import com.wuyanteam.campustaskplatform.utils.Result;

public interface PhotoWallService extends MPJDeepService <photoWall> {
    public Result findPhotoWall(int id);
}
