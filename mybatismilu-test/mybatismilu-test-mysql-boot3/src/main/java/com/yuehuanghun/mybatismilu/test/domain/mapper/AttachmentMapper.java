package com.yuehuanghun.mybatismilu.test.domain.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.yuehuanghun.mybatis.milu.BaseMapper;
import com.yuehuanghun.mybatismilu.test.domain.entity.Attachment;

@Mapper
public interface AttachmentMapper extends BaseMapper<Attachment, Long> {

}
