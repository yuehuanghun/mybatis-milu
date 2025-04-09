package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import lombok.Data;

@Entity
@Data
public class ClassTeacherRel {

	@Id
	private Long id;
	
	private Long classId;
	
	private Long teacherId;
	
	private LocalDateTime addTime;
	
	@OneToMany
	@JoinColumn(name = "teacher_id", referencedColumnName = "id")
	private List<Teacher> teachers;
}
