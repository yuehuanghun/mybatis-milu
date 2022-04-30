package com.yuehuanghun.mybatis.milu.example.example1.service.impl;

import org.springframework.stereotype.Service;

import com.yuehuanghun.mybatis.milu.example.example1.domain.ExampleOne;
import com.yuehuanghun.mybatis.milu.example.example1.mapper.ExampleOneMapper;
import com.yuehuanghun.mybatis.milu.example.example1.service.IExampleOneService;
import com.yuehuanghun.mybatis.milu.ext.BaseServiceImpl;

@Service
public class ExampleOneServiceImpl extends BaseServiceImpl<ExampleOne, Long, ExampleOneMapper> implements IExampleOneService {

}
