package br.com.wleydson.libraryapi.api.controller;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.modelmapper.ModelMapper;

import br.com.wleydson.libraryapi.api.dto.BookDTO;
import br.com.wleydson.libraryapi.api.dto.LoanDTO;
import br.com.wleydson.libraryapi.api.dto.LoanFilterDTO;
import br.com.wleydson.libraryapi.api.dto.ReturnedLoanDTO;
import br.com.wleydson.libraryapi.model.entity.Book;
import br.com.wleydson.libraryapi.model.entity.Loan;
import br.com.wleydson.libraryapi.service.BookService;
import br.com.wleydson.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@Api("Loan Api")
@RequiredArgsConstructor
public class LoanController {

	private final LoanService loanService;
	private final BookService bookService;
	private final ModelMapper modelMapper;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation("Create a loan")
	public Long create( @RequestBody LoanDTO dto) {
		Book book = bookService
				.getBookByIsbn(dto.getIsbn())
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn") );
		
		Loan entity = Loan.builder()
				.book(book)
				.customer(dto.getCustomer())
				.loanDate(LocalDate.now())
				.build();
		
		entity = loanService.save(entity);
		
		return entity.getId();
	}
	
	@PatchMapping("{id}")
	@ApiOperation("Returned a loan")
	public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO returnedDTO)  {
		Loan loan = loanService.getByid(id)
				.orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
		loan.setReturned(returnedDTO.getReturned());
		
		loanService.update(loan);
	}
	
	@GetMapping
	@ApiOperation("Find loan")
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest) {
        Page<Loan> result = loanService.find(dto, pageRequest);
        List<LoanDTO> loans = result
                .getContent()
                .stream()
                .map(entity -> {

                    Book book = entity.getBook();
                    BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;

                }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(loans, pageRequest, result.getTotalElements());
    }
	
}
