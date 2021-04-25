package com.yuehuanghun.mybatismilu.test.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.pagehelper.PageHelper;
import com.yuehuanghun.mybatismilu.test.domain.entity.Student;
import com.yuehuanghun.mybatismilu.test.service.StudentService;

@RestController
@RequestMapping("/student")
public class Studentcontroller {
	@Autowired
	private StudentService studentService;

	@GetMapping("/{id}")
	public Student getById(@PathVariable Long id) {
		return studentService.getById(id).orElse(null);
	}
	
	@GetMapping("/list")
	public List<Student> list(Student param){
		PageHelper.startPage(1, 10);
		return studentService.getByCriteria(p -> p.gt("id", param.getId()));
	}
	
	@PostMapping
	public void save(@RequestBody Student student) {
		studentService.add(student);
	}
	
	@PostMapping("/batch")
	public void batchSave() {
		List<Student> list = new ArrayList<>(50);
		Student student;
		for(int i = 0; i < 50; i ++) {
			student = new Student();
			student.setAge(9);
			student.setClassId(1L);
			student.setName(randomName());
			list.add(student);
		}
		
		studentService.batchAdd(list);
	}
	
	private static String randomName() {
		return new String(new char[] {getRandomChar(), getRandomChar()});
	}
	
	private static char getRandomChar() {
        return (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
    }
}
