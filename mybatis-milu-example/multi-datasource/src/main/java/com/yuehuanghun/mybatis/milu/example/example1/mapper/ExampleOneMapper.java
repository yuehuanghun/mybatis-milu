package com.yuehuanghun.mybatis.milu.example.example1.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatis.milu.example.example1.domain.ExampleOne;

@Mapper
public interface ExampleOneMapper extends BaseMapper<ExampleOne, Long> {

}
