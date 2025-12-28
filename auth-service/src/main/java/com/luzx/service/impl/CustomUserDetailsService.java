package com.luzx.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.luzx.mapper.UserMapper;
import com.luzx.model.User;
import com.luzx.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.Wrapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService
{


    @Resource
    private UserRepository userRepository;

    @Override
//    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException 
    {
        Optional<User> optional = userRepository.findByUsername(username);
        if (!optional.isPresent())
        {
            throw new UsernameNotFoundException("用户未找到: " + username);
        }
        User user = optional.get();
        user.setRoles(Collections.singletonList("USER"));
        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(User user)
    {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getLoginName(),
                user.getPassword(),
                true,
                true, true, true,
                authorities
        );
    }


}
