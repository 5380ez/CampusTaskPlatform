package com.wuyanteam.campustaskplatform.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.yulichang.base.MPJBaseService;
import com.wuyanteam.campustaskplatform.entity.Notification;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@Service
public class NotificationService implements MPJBaseService<Notification> {
    @Override
    public boolean removeById(Serializable id) {
        return MPJBaseService.super.removeById(id);
    }

    @Override
    public boolean saveBatch(Collection<Notification> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<Notification> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<Notification> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(Notification entity) {
        return false;
    }

    @Override
    public Notification getOne(Wrapper<Notification> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<Notification> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<Notification> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<Notification> getBaseMapper() {
        return null;
    }

    @Override
    public Class<Notification> getEntityClass() {
        return null;
    }
}
