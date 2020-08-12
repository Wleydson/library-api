package br.com.wleydson.libraryapi.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.wleydson.libraryapi.api.dto.LoanDTO;
import br.com.wleydson.libraryapi.api.dto.LoanFilterDTO;
import br.com.wleydson.libraryapi.api.dto.ReturnedLoanDTO;
import br.com.wleydson.libraryapi.exception.BusinessException;
import br.com.wleydson.libraryapi.model.entity.Book;
import br.com.wleydson.libraryapi.model.entity.Loan;
import br.com.wleydson.libraryapi.service.BookService;
import br.com.wleydson.libraryapi.service.LoanService;
import br.com.wleydson.libraryapi.service.LoanServiceTest;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController .class)
@AutoConfigureMockMvc
public class LoanControllerTest {

	static String LOAN_API = "/api/loans/";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService bookService;
	
	@MockBean
	private LoanService loanService;
	
	@Test
	@DisplayName("must make a loan")
	public void createLoanTest() throws Exception{	
		LoanDTO dto = LoanDTO.builder().isbn("123").customer("Wleydson").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().id(1L).isbn("123").build();
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn( Optional.of(book) );

		Loan loan = Loan.builder().id(1L).customer("Wleydson").book(book).loanDate(LocalDate.now()).build();
		BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn( loan );
		
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(LOAN_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect( status().isCreated() )
			.andExpect( content().string("1") );
		
	}
	
	@Test
	@DisplayName("error when searching for book")
	public void invalidCreateLoanTest() throws Exception{	
		LoanDTO dto = LoanDTO.builder().isbn("123").customer("Wleydson").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().id(1L).isbn("123").build();
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn( Optional.empty() );

		Loan loan = Loan.builder().id(1L).customer("Wleydson").book(book).loanDate(LocalDate.now()).build();
		BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn( loan );
		
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(LOAN_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
			.andExpect( status().isBadRequest() )
			.andExpect( jsonPath("errors", Matchers.hasSize(1)) )
			.andExpect( jsonPath("errors[0]").value("Book not found for passed isbn"));
		
	}
	
	@Test
	@DisplayName("error in borrowing a book already borrowed")
	public void LoanedBookErrorOnCreateLoanTest() throws Exception{	
		
		LoanDTO dto = LoanDTO.builder().isbn("123").customer("Wleydson").build();
		String json = new ObjectMapper().writeValueAsString(dto);
		
		Book book = Book.builder().id(1L).isbn("123").build();
		BDDMockito.given(bookService.getBookByIsbn("123")).willReturn( Optional.of(book) );
		
		BDDMockito.given( loanService.save(Mockito.any(Loan.class)) ).willThrow(new BusinessException("Book already loaned"));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post(LOAN_API)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json);
		
		mvc.perform(request)
		.andExpect( status().isBadRequest() )
		.andExpect( jsonPath("errors", Matchers.hasSize(1)) )
		.andExpect( jsonPath("errors[0]").value("Book already loaned"));
	}
	
	@Test
    @DisplayName("return a book")
    public void returnBookTest() throws Exception{
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        Loan loan = Loan.builder().id(1l).build();
        
        BDDMockito
	        .given( loanService.getByid(Mockito.anyLong()) )
	        .willReturn( Optional.of(loan) );

        String json = new ObjectMapper().writeValueAsString(dto);

        mvc.perform(
            patch(LOAN_API.concat("/1"))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .content(json)
        ).andExpect( status().isOk() );

        Mockito.verify(loanService, Mockito.times(1)).update(loan);
    }
	
	@Test
    @DisplayName("return 404 - Book not found")
    public void returnInexistentBookTest() throws Exception{
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(loanService.getByid(Mockito.anyLong())).willReturn(Optional.empty());

        mvc.perform(
                patch(LOAN_API.concat("/1"))
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
        ).andExpect( status().isNotFound() );

    }
	
	@Test
    @DisplayName("search with filter")
    public void findLoansTest() throws Exception{
        Long id = 1l;
        Loan loan = LoanServiceTest.createLoan();
        loan.setId(id);
        Book book = Book.builder().id(1l).isbn("321").build();
        loan.setBook(book);

        BDDMockito
        	.given( loanService.find( Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)) )
        	.willReturn( new PageImpl<Loan>( Arrays.asList(loan), PageRequest.of(0,10), 1 ) );

        String queryString = String.format("?isbn=%s&customer=%s&page=0&size=10",
                book.getIsbn(), loan.getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc
                .perform( request )
                .andExpect( status().isOk() )
                .andExpect( jsonPath("content", Matchers.hasSize(1)))
                .andExpect( jsonPath("totalElements").value(1) )
                .andExpect( jsonPath("pageable.pageSize").value(10) )
                .andExpect( jsonPath("pageable.pageNumber").value(0))
        ;
    }

}
