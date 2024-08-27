package com.wuyanteam.campustaskplatform.service.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyanteam.campustaskplatform.Reposity.photoWallDao;
import com.wuyanteam.campustaskplatform.entity.photoWall;
import com.wuyanteam.campustaskplatform.service.PhotoWallService;
import com.wuyanteam.campustaskplatform.utils.Result;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class PhotoWallServiceImpl implements PhotoWallService {
    @Resource
    private photoWallDao photowallDao;
    @Override
    public Result findPhotoWall(int id){
        List<photoWall> photowall=photowallDao.findByUserId(id);
        if(photowall.size()>0){
            return Result.success(photowall);
        }else {
            return Result.error("照片墙为空");
        }
    }

    @Override
    public boolean saveBatch(Collection<photoWall> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<photoWall> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<photoWall> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(photoWall entity) {
        return false;
    }

    @Override
    public photoWall getOne(Wrapper<photoWall> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<photoWall> queryWrapper) {
        return Collections.emptyMap();
    }

    @Override
    public <V> V getObj(Wrapper<photoWall> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<photoWall> getBaseMapper() {
        return null;
    }

    @Override
    public Class<photoWall> getEntityClass() {
        return null;
    }
}
