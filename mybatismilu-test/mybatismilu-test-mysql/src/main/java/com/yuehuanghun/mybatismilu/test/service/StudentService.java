package com.yuehuanghun.mybatismilu.test.service;

import com.yuehuanghun.mybatis.milu.ext.BaseService;
import com.yuehuanghun.mybatismilu.test.domain.entity.Student;
import com.yuehuanghun.mybatismilu.test.domain.mapper.StudentMapper;

public interface StudentService extends BaseService<Student, Long, StudentMapper> {

}
