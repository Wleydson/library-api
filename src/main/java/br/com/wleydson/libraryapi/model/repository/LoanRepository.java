package br.com.wleydson.libraryapi.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.wleydson.libraryapi.model.entity.Book;
import br.com.wleydson.libraryapi.model.entity.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long>{

	boolean existsByBookAndNotReturned(Book book);

}
