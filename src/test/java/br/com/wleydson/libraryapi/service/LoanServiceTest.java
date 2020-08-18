package br.com.wleydson.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.wleydson.libraryapi.api.dto.LoanFilterDTO;
import br.com.wleydson.libraryapi.exception.BusinessException;
import br.com.wleydson.libraryapi.model.entity.Book;
import br.com.wleydson.libraryapi.model.entity.Loan;
import br.com.wleydson.libraryapi.model.repository.LoanRepository;
import br.com.wleydson.libraryapi.service.impl.LoanServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

	LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Must save a loan")
    public void saveLoanTest(){
        Book book = Book.builder().id(1l).build();
        String customer = "Wleydson";

        Loan savingLoan =
                Loan.builder()
                .book(book)
                .customer(customer)
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                    .id(1l)
                    .loanDate(LocalDate.now())
                    .customer(customer)
                    .book(book).build();


        when( repository.existsByBookAndNotReturned(book) ).thenReturn(false);
        when( repository.save(savingLoan) ).thenReturn( savedLoan );

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Must launch business error when saving a loan with book already borrowed")
    public void loanedBookSaveTest(){
        Book book = Book.builder().id(1l).build();
        String customer = "Wleydson";

        Loan savingLoan =
                Loan.builder()
                        .book(book)
                        .customer(customer)
                        .loanDate(LocalDate.now())
                        .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned");

        verify(repository, never()).save(savingLoan);

    }
    
    @Test
    @DisplayName("find by id")
    public void getLoanDetaisTest(){
        Long id = 1l;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when( repository.findById(id) ).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getByid(id);

        assertThat(result.isPresent()).isTrue();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify( repository ).findById(id);

    }

    @Test
    @DisplayName("update loan")
    public void updateLoanTest(){
        Loan loan = createLoan();
        loan.setId(1l);
        loan.setReturned(true);

        when( repository.save(loan) ).thenReturn( loan );

        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository).save(loan);
    }
    
    @Test
    @DisplayName("search with filter")
    public void findLoanTest(){
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer("Fulano").isbn("321").build();

        Loan loan = createLoan();
        loan.setId(1l);
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Loan> lista = Arrays.asList(loan);

        Page<Loan> page = new PageImpl<Loan>(lista, pageRequest, lista.size());
        when( repository.findByBookIsbnOrCustomer(
                    Mockito.anyString(),
                    Mockito.anyString(),
                    Mockito.any(PageRequest.class))
        )
                .thenReturn(page);

        Page<Loan> result = service.find( loanFilterDTO, pageRequest );


        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
    
    @Test
    @DisplayName("Get loans by book")
    public void getLoansByBookTest(){
        Long id = 1l;
        
        List<Loan> lista = Arrays.asList(createLoanById(id));
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Loan> page = new PageImpl<Loan>(lista,  pageRequest, lista.size());

        Mockito
        	.when( repository.findByBook(Mockito.any(Book.class), Mockito.any(PageRequest.class)) )
        	.thenReturn(page);

        Page<Loan> result = service.getLoansByBook(Book.builder().id(id).build(), pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }
	
    public static Loan createLoan(){
        Book book = Book.builder().id(1l).build();
        String customer = "Wleydson";

        return Loan.builder()
                        .book(book)
                        .customer(customer)
                        .loanDate(LocalDate.now())
                        .build();
    }
    
    public Loan createLoanById(Long id){
        Book book = Book.builder().id(1l).build();
        String customer = "Wleydson";

        return Loan.builder()
                        .book(book)
                        .customer(customer)
                        .id(id)
                        .loanDate(LocalDate.now())
                        .build();
    }
}
