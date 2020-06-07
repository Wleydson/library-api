package br.com.wleydson.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.wleydson.libraryapi.model.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	BookRepository repository;
	
	@Test
	@DisplayName("returns true when a book with informed isbn exists")
	public void returnTrueWhenIsbnExists() {
		Book book = entityManager.persist(createNewBook());
		
		boolean exists = repository.existsByIsbn(book.getIsbn());
		
		assertThat(exists).isTrue();
	}
	
	@Test
	@DisplayName("returns false when there is no book with informed isbn")
	public void returnFalseWhenIsbnExists() {
		String isbn = "123123";
		
		boolean exists = repository.existsByIsbn(isbn);
		
		assertThat(exists).isFalse();
	}
	
	private Book createNewBook() {
		return Book.builder().title("My book").author("Wleydson").isbn("123123").build();
	}
}
