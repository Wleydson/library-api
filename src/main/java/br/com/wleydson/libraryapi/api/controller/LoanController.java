package br.com.wleydson.libraryapi.api.controller;


import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.wleydson.libraryapi.api.dto.LoanDTO;
import br.com.wleydson.libraryapi.model.entity.Book;
import br.com.wleydson.libraryapi.model.entity.Loan;
import br.com.wleydson.libraryapi.service.BookService;
import br.com.wleydson.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

	private final LoanService loanService;
	private final BookService bookService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Long create( @RequestBody LoanDTO dto) {
		Book book = bookService
				.getBookIsbn(dto.getIsbn())
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn") );
		
		Loan entity = Loan.builder()
				.book(book)
				.custumer(dto.getCustomer())
				.loanDate(LocalDate.now())
				.build();
		
		entity = loanService.save(entity);
		
		return entity.getId();
	}
	
}
