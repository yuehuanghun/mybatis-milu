package com.yuehuanghun.mybatismilu.test.domain.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatismilu.test.domain.entity.TeachingPlan;

@Mapper
public interface TeachingPlanMapper extends BaseMapper<TeachingPlan, Long> {

}
