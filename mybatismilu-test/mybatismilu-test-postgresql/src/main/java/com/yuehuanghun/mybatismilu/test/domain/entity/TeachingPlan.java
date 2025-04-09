package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

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
