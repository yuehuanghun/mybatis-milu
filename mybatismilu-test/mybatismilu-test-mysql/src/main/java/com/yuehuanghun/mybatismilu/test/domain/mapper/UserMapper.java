package com.yuehuanghun.mybatismilu.test.domain.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatismilu.test.domain.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User, Long> {

}
