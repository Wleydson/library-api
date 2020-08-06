package br.com.wleydson.libraryapi.api.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.validation.BindingResult;

public class ApiErrors {

	private List<String> errors;
	

	public ApiErrors(BindingResult bindingResult) {
		this.errors = new ArrayList<>();
		bindingResult.getAllErrors().forEach(error -> this.errors.add( error.getDefaultMessage() ) );
	}
	
	public ApiErrors(String messageError) {
		this.errors = Arrays.asList(messageError);
	}

	public List<String> getErrors() {
		return errors;
	}

}