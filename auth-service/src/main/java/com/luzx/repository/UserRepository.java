package com.luzx.repository;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.luzx.mapper.UserMapper;
import com.luzx.model.User;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository
{

    @Resource
    private UserMapper userMapper;


    public Optional<User> findByUsername(String username)
    {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = Wrappers.lambdaQuery();
        userLambdaQueryWrapper.eq(User::getLoginName, username);
        List<User> users = userMapper.selectList(userLambdaQueryWrapper);
        if (CollectionUtil.isNotEmpty(users))
        {
            return Optional.of(users.get(0));
        }
        return Optional.empty();
    }

    public void save(User user)
    {
        userMapper.insert(user);
    }

    public Boolean existsByUsername(String username)
    {
        Optional<User> optional = this.findByUsername(username);
        return optional.isPresent();
    }

}
