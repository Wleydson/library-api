package br.com.wleydson.libraryapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.wleydson.libraryapi.api.dto.LoanFilterDTO;
import br.com.wleydson.libraryapi.model.entity.Book;
import br.com.wleydson.libraryapi.model.entity.Loan;

public interface LoanService {

	Loan save(Loan loan);
	
	Loan update(Loan loan);

	Optional<Loan> getByid(Long id);

	Page<Loan> find(LoanFilterDTO loanFilterDTO, Pageable pageable);

	Page<Loan> getLoansByBook(Book book, Pageable pageable);

	List<Loan> getAllLateLoans();
}
