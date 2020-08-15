package br.com.wleydson.libraryapi.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.wleydson.libraryapi.api.dto.LoanFilterDTO;
import br.com.wleydson.libraryapi.exception.BusinessException;
import br.com.wleydson.libraryapi.model.entity.Book;
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

	@Override
	public Loan update(Loan loan) {
        return repository.save(loan);
	}

	@Override
	public Optional<Loan> getByid(Long id) {
        return repository.findById(id);
	}

	@Override
	public Page<Loan> find(LoanFilterDTO loanFilterDTO, Pageable pageable ) {
		return repository.findByBookIsbnOrCustomer(loanFilterDTO.getIsbn(), loanFilterDTO.getCustomer(), pageable);
	}

	@Override
	public Page<Loan> getLoansByBook(Book book, Pageable pageable) {
		return repository.findByBook(book, pageable);
	}

	@Override
	public List<Loan> getAllLateLoans() {
		final Integer loanDays = 4;
		LocalDate threeDaysAgo = LocalDate.now().minusDays(loanDays);
		return repository.findByLoanDateLessThanAndNotReturned(threeDaysAgo);
	}

}
