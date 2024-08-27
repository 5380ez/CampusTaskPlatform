package com.wuyanteam.campustaskplatform.service.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wuyanteam.campustaskplatform.entity.Task;
import com.wuyanteam.campustaskplatform.mapper.TaskMapper;
import com.wuyanteam.campustaskplatform.service.TaskService;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
    public void checkAndSetTimeoutTasks() {
        // 查询所有状态为 "incomplete" 和 "un-taken" 的任务
        QueryWrapper<Task> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.in("state", "incomplete", "un-taken");
        QueryWrapper<Task> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.in("state", "unconfirmed");

        List<Task> tasks1 = this.list(queryWrapper1);
        List<Task> tasks2 = this.list(queryWrapper1);
// 遍历这些任务，检查是否已经超时
        for (Task task1 : tasks1) {
            LocalDateTime now = LocalDateTime.now();
            Timestamp nowTime = Timestamp.from(now.atZone(ZoneId.systemDefault()).toInstant());
            if (task1.getDueTime().compareTo(nowTime) < 0) {
                this.updateById(task1);
            }
        }
        for (Task task2 : tasks2) {
            LocalDateTime ago30 = LocalDateTime.now().minusMinutes(30);
            Timestamp ago30t = Timestamp.from(ago30.atZone(ZoneId.systemDefault()).toInstant());
            if (task2.getFinishTime() != null) {
                if (ago30t.compareTo(task2.getFinishTime()) >= 0) {
                    task2.setState("complete");
                    this.updateById(task2);
                }
            }
        }
    }

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

 /*   @Override
    public BaseMapper<Task> getBaseMapper() {
        return null;
    }*/

    @Override
    public Class<Task> getEntityClass() {
        return null;
    }
}
