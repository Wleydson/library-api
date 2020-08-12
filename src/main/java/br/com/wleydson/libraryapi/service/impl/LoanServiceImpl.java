package br.com.wleydson.libraryapi.service.impl;

import org.springframework.stereotype.Service;

import br.com.wleydson.libraryapi.exception.BusinessException;
import br.com.wleydson.libraryapi.model.entity.Loan;
import br.com.wleydson.libraryapi.model.repository.LoanRepository;
import br.com.wleydson.libraryapi.service.LoanService;

@Service
public class LoanServiceImpl implements LoanService{

	private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }
	    
	@Override
	public Loan save(Loan loan) {
		if( repository.existsByBookAndNotReturned(loan.getBook()) ){
            throw new BusinessException("Book already loaned");
        }
        return repository.save(loan);
	}

}
