package com.yuehuanghun.mybatismilu.test.domain.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

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
	
	@OneToMany
	@JoinColumn(name = "class_id", referencedColumnName = "id")
	private List<Classs> classes;
}
