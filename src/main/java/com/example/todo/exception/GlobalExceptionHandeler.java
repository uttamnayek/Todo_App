package com.example.todo.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandeler {

	@ExceptionHandler(Exception.class)
	public void handelGlobelException(Exception e) {
		System.out.println(e.getMessage());
	}
}
