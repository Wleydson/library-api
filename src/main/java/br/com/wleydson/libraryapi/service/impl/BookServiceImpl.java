package br.com.wleydson.libraryapi.service.impl;

import org.springframework.stereotype.Service;

import br.com.wleydson.libraryapi.model.entity.Book;
import br.com.wleydson.libraryapi.model.repository.BookRepository;
import br.com.wleydson.libraryapi.service.BookService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService{

	private BookRepository repository;
	
	@Override
	public Book save(Book book) {
		return repository.save(book);
	}

}
