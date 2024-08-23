package com.wuyanteam.campustaskplatform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.yulichang.extension.mapping.base.MPJDeepService;
import com.wuyanteam.campustaskplatform.entity.User;

public interface UserService extends MPJDeepService<User>
{
    User LoginService(String username, String password);
    User RegisterService(User user);
    User InfoService(String token);
}
