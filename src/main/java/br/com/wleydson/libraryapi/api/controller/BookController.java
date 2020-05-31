package br.com.wleydson.libraryapi.api.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.wleydson.libraryapi.api.dto.BookDTO;
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
	public BookDTO create( @RequestBody BookDTO dto) {
		Book entity = modelMapper.map(dto, Book.class);
		entity = service.save(entity);
		
		return modelMapper.map(entity, BookDTO.class);
	}
}
