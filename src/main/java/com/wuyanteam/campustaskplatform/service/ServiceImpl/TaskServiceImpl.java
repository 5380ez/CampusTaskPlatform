package com.wuyanteam.campustaskplatform.service.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyanteam.campustaskplatform.entity.Task;
import com.wuyanteam.campustaskplatform.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

@Service
public class TaskServiceImpl implements TaskService {
    @Override
    public boolean saveBatch(Collection<Task> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<Task> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<Task> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(Task entity) {
        return false;
    }

    @Override
    public Task getOne(Wrapper<Task> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<Task> queryWrapper) {
        return null;
    }

    @Override
    public <V> V getObj(Wrapper<Task> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<Task> getBaseMapper() {
        return null;
    }

    @Override
    public Class<Task> getEntityClass() {
        return null;
    }
}
