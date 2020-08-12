package br.com.wleydson.libraryapi.service;

import java.util.Optional;

import br.com.wleydson.libraryapi.model.entity.Loan;

public interface LoanService {

	Loan save(Loan loan);
	
	Loan update(Loan loan);

	Optional<Loan> getByid(Long id);

}
