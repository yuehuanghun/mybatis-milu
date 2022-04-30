package com.yuehuanghun.mybatis.milu.example.example2.service.impl;

import org.springframework.stereotype.Service;

import com.yuehuanghun.mybatis.milu.example.example2.domain.ExampleTwo;
import com.yuehuanghun.mybatis.milu.example.example2.mapper.ExampleTwoMapper;
import com.yuehuanghun.mybatis.milu.example.example2.service.IExampleTwoService;
import com.yuehuanghun.mybatis.milu.ext.BaseServiceImpl;

@Service
public class ExampleTwoServiceImpl extends BaseServiceImpl<ExampleTwo, Long, ExampleTwoMapper> implements IExampleTwoService {

}
