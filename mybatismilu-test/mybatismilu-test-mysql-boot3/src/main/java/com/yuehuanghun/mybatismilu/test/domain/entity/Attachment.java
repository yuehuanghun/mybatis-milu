package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.yuehuanghun.mybatis.milu.annotation.alias.id.SnowflakeId;

import lombok.Data;

@Entity
@Data
public class Attachment {

	@SnowflakeId
	private Long id;
	
	private String fileName;
	
	private String filePath;
	
	private LocalDateTime createTime;
	
	@ManyToMany(mappedBy = "attachments")
	private List<TeachingPlan> plans;
}
