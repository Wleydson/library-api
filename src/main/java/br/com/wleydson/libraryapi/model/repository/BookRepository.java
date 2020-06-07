package br.com.wleydson.libraryapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.wleydson.libraryapi.model.entity.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

	boolean existsByIsbn(String isbn);

}
