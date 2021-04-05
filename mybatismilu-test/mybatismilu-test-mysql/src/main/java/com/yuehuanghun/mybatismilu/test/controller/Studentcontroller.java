package com.yuehuanghun.mybatismilu.test.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yuehuanghun.mybatismilu.test.domain.entity.Student;
import com.yuehuanghun.mybatismilu.test.service.StudentService;

@RestController
@RequestMapping("/student")
public class Studentcontroller {
	@Autowired
	private StudentService studentService;

	@GetMapping("/{id}")
	public Student getById(@PathVariable Long id) {
		return studentService.getById(id).get();
	}
	
	@GetMapping("/list")
	public List<Student> list(Student param){
		return studentService.getByExample(param);
	}
}
