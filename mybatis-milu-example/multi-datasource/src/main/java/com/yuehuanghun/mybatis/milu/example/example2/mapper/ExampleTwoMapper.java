package com.yuehuanghun.mybatis.milu.example.example2.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.example.example2.domain.ExampleTwo;

@Mapper
public interface ExampleTwoMapper extends BaseMapper<ExampleTwo, Long> {

}
