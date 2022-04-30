package com.yuehuanghun.mybatis.milu.example;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yuehuanghun.mybatis.milu.example.example1.domain.ExampleOne;
import com.yuehuanghun.mybatis.milu.example.example1.service.IExampleOneService;
import com.yuehuanghun.mybatis.milu.example.example2.domain.ExampleTwo;
import com.yuehuanghun.mybatis.milu.example.example2.service.IExampleTwoService;

@RestController
@RequestMapping("/example")
public class ExampleController {
	
	@Autowired
	private IExampleOneService exampleOneService;
	@Autowired
	private IExampleTwoService exampleTwoService;

	@PostMapping("/exampleOne")
	public ExampleOne addExampleOne() {
		ExampleOne exampleOne = new ExampleOne(randomName());
		exampleOneService.add(exampleOne);
		return exampleOne;
	}
	
	@PostMapping("/exampleTwo")
	public ExampleTwo addExampleTwo() {
		ExampleTwo exampleTwo = new ExampleTwo(UUID.randomUUID().toString(), randomName());
		exampleTwoService.add(exampleTwo);
		return exampleTwo;
	}
	
	@GetMapping("/exampleOne/{id}")
	public ExampleOne getExampleOne(@PathVariable Long id) {
		return exampleOneService.getById(id).orElse(null);
	}
	
	@GetMapping("/exampleTwo/{id}")
	public ExampleTwo getExampleTwo(@PathVariable Long id) {
		return exampleTwoService.getById(id).orElse(null);
	}
	
	private static String randomName() {
		return new String(new char[] {getRandomChar(), getRandomChar()});
	}
	
	private static char getRandomChar() {
        return (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
    }
}
