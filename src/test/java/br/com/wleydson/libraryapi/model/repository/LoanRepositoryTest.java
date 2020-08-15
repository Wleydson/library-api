package br.com.wleydson.libraryapi.model.repository;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.wleydson.libraryapi.model.entity.Book;
import br.com.wleydson.libraryapi.model.entity.Loan;

import static br.com.wleydson.libraryapi.model.repository.BookRepositoryTest.createNewBook;
import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	LoanRepository repository;
	
	@Test
	@DisplayName("book exists and is not returned")
	public void existsByBookAndNotReturned() {
		Loan loan = createAndPersistLoan(LocalDate.now());
        Book book = loan.getBook();
        
        boolean exists = repository.existsByBookAndNotReturned(book);

        assertThat(exists).isTrue();
        
	}
	
	@Test
    @DisplayName("find loan")
    public void findByBookIsbnOrCustomerTest(){
        Loan loan = createAndPersistLoan(LocalDate.now());

        Page<Loan> result = repository.findByBookIsbnOrCustomer("123", "Fulano", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }
	
	@Test
    @DisplayName("returns unsecured loan")
    public void findByLoanDateLessThanAndNotReturnedTest(){
        Loan loan = createAndPersistLoan( LocalDate.now().minusDays(5) );

        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).hasSize(1).contains(loan);
    }
	
	@Test
    @DisplayName("there are no loans not returned")
    public void notFindByLoanDateLessThanAndNotReturnedTest(){
        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).isEmpty();
    }
	
	public Loan createAndPersistLoan(LocalDate loanDate){
        Book book = createNewBook();
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(loanDate).build();
        entityManager.persist(loan);

        return loan;
    }
}
