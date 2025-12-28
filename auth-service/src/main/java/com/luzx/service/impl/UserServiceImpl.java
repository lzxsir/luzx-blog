package com.luzx.service.impl;

import com.luzx.model.User;
import com.luzx.mapper.UserMapper;
import com.luzx.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author 昱
 * @since 2025-12-28 18:08:25
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
