package br.com.wleydson.libraryapi.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.wleydson.libraryapi.exception.BusinessException;
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
		if(repository.existsByIsbn(book.getIsbn())) {
			throw new BusinessException("isbn already registered");
		}
		return repository.save(book);
	}

	@Override
	public Optional<Book> getById(Long id) {		
		return repository.findById(id);
	}

	@Override
	public void delete(Book book) {
		if(book == null || book.getId() == null )
			throw new IllegalArgumentException("Book id cant be null.");
		
		repository.delete(book);
	}

	@Override
	public Book update(Book book) {
		if(book == null || book.getId() == null )
			throw new IllegalArgumentException("Book id cant be null.");
		
		return repository.save(book);
	}

	@Override
	public Page<Book> find(Book filter, Pageable pageRequest) {
		Example<Book> example = Example.of(filter, 
				ExampleMatcher.matching()
							.withIgnoreCase()
							.withIgnoreNullValues()
							.withStringMatcher( ExampleMatcher.StringMatcher.CONTAINING )
		);
				
		return repository.findAll( example, pageRequest);
	}

}
