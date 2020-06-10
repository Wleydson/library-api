package br.com.wleydson.libraryapi.service;

import java.util.Optional;

import br.com.wleydson.libraryapi.model.entity.Book;

public interface BookService{

	Book save(Book book);

	Optional<Book> getById(Long id);

	void delete(Book book);

	Book update(Book book);

}
