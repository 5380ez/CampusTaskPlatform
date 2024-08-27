package com.wuyanteam.campustaskplatform.service;

import com.github.yulichang.extension.mapping.base.MPJDeepService;
import com.wuyanteam.campustaskplatform.entity.User;
import com.wuyanteam.campustaskplatform.utils.Result;

public interface UserService extends MPJDeepService<User>
{
    User LoginService(String username, String password);
    Result<User> RegisterService(User user);
    User InfoService(String token);
    Result ResetPassword(User user);
    void UpdateLevelService();
}
