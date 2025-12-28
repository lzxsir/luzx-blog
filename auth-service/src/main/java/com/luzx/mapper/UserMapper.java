package com.luzx.mapper;

import com.luzx.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户信息表 Mapper 接口
 * </p>
 *
 * @author 昱
 * @since 2025-12-28 18:08:25
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
