package br.com.wleydson.libraryapi.service.impl;

import java.util.Optional;

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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Book book) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Book update(Book book) {
		// TODO Auto-generated method stub
		return null;
	}

}
