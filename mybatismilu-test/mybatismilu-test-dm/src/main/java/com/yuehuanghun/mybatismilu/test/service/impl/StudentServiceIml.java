package com.yuehuanghun.mybatismilu.test.service.impl;

import org.springframework.stereotype.Service;

import com.yuehuanghun.mybatis.milu.ext.BaseServiceImpl;
import com.yuehuanghun.mybatismilu.test.domain.entity.Student;
import com.yuehuanghun.mybatismilu.test.domain.mapper.StudentMapper;
import com.yuehuanghun.mybatismilu.test.service.StudentService;

@Service
public class StudentServiceIml extends BaseServiceImpl<Student, Long, StudentMapper> implements StudentService {

}
