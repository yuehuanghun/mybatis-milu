package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.yuehuanghun.mybatis.milu.annotation.alias.id.SnowflakeId;

import lombok.Data;

@Entity
@Data
public class TeachingPlan {

	@SnowflakeId
	private Long id;
	
	private String planName;
	
	private String planDescp;
	
	private String attachmentType;
	
	private LocalDateTime createTime;
	
	@ManyToMany
	@JoinTable(name = "attachment_ref", joinColumns = {@JoinColumn(name = "ref_id", referencedColumnName = "id"), @JoinColumn(name = "attachment_type")}, inverseJoinColumns = @JoinColumn(name = "attachment_id", referencedColumnName = "id"))
	private List<Attachment> attachments;
}
