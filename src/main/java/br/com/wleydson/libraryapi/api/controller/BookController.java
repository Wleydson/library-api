package br.com.wleydson.libraryapi.api.controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.wleydson.libraryapi.api.dto.BookDTO;
import br.com.wleydson.libraryapi.api.exception.ApiErrors;
import br.com.wleydson.libraryapi.exception.BusinessException;
import br.com.wleydson.libraryapi.model.entity.Book;
import br.com.wleydson.libraryapi.service.BookService;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/books/")
@AllArgsConstructor
public class BookController {

	private ModelMapper modelMapper;
	
	private BookService service;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDTO create( @RequestBody @Valid BookDTO dto) {
		Book entity = modelMapper.map(dto, Book.class);
		entity = service.save(entity);
		
		return modelMapper.map(entity, BookDTO.class);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleValidationException(MethodArgumentNotValidException exp) {
		BindingResult bindingResult = exp.getBindingResult();
		return new ApiErrors(bindingResult);
	}
	
	@ExceptionHandler(BusinessException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiErrors handleBusinessException(BusinessException exp) {
		String messageError = exp.getMessage();
		return new ApiErrors(messageError);
	}
	
}
