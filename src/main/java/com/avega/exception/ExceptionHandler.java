package com.avega.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

	@org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
	public ResponseEntity<String> handler(Exception ex){
		return ResponseEntity.badRequest().body(ex.getLocalizedMessage());
	}

}
