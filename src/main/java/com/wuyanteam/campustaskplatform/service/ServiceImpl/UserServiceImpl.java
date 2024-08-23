package com.wuyanteam.campustaskplatform.service.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wuyanteam.campustaskplatform.Reposity.UserDao;
import com.wuyanteam.campustaskplatform.entity.User;
import com.wuyanteam.campustaskplatform.service.UserService;
import com.wuyanteam.campustaskplatform.utils.JWTUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Override
    public User LoginService(String username, String password) {
        User user = userDao.findByUsernameAndPassword(username, password);
        // 重要信息置空
        if (user != null) {
            Date now = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            user.setLastLoginTime(ft.format(now));
            user=userDao.save(user);
            user.setPassword("");
        }
        return user;
    }

    @Override
    public User RegisterService(User user) {
        if (userDao.findByUsername(user.getUsername()) != null) {
            // 无法注册
            return null;
        } else {
            //返回创建好的用户对象(带uid)
            user.setExp(0);
            user.setLevel(1);
            Date now = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            user.setAccCrtTime(ft.format(now));
            user.setLastLoginTime(ft.format(now));
            user.setLikeCount(0);
            user.setTakeNum(0);
            user.setPublishNum(0);
            user.setFinishNum(0);
            user.setBalance(0);
            User newUser = userDao.save(user);
            if (newUser != null) {
                newUser.setPassword("");
            }
            return newUser;
        }
    }

    @Override
    public User InfoService(String token) {//通过token返回user
        if (JWTUtils.verifyToken(token)) {
            String username = JWTUtils.getClaimsByToken(token).getSubject();
            if (username == null) {
                return null;
            }
        User user = userDao.findByUsername(username);
        Date IssuedAt=JWTUtils.getClaimsByToken(token).getIssuedAt();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        if(simpleDateFormat.format(IssuedAt).compareTo(user.getLastLoginTime())==0){//当最后登录时间等于token刷新时间时，token才有效，避免重复登录时用户之前生成的ID还有效的情况发生
            return user;
            }
        }
        return null;
    }

    @Override
    public boolean saveBatch(Collection<User> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdateBatch(Collection<User> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean updateBatchById(Collection<User> entityList, int batchSize) {
        return false;
    }

    @Override
    public boolean saveOrUpdate(User entity) {
        return false;
    }

    @Override
    public User getOne(Wrapper<User> queryWrapper, boolean throwEx) {
        return null;
    }

    @Override
    public Map<String, Object> getMap(Wrapper<User> queryWrapper) {
        return Collections.emptyMap();
    }

    @Override
    public <V> V getObj(Wrapper<User> queryWrapper, Function<? super Object, V> mapper) {
        return null;
    }

    @Override
    public BaseMapper<User> getBaseMapper() {
        return null;
    }

    @Override
    public Class<User> getEntityClass() {
        return null;
    }
}