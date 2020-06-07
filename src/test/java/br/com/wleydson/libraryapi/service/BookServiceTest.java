package br.com.wleydson.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.wleydson.libraryapi.exception.BusinessException;
import br.com.wleydson.libraryapi.model.entity.Book;
import br.com.wleydson.libraryapi.model.repository.BookRepository;
import br.com.wleydson.libraryapi.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

	BookService service;

	@MockBean
	BookRepository repository;

	@BeforeEach
	public void setUp() {
		this.service = new BookServiceImpl(repository);
	}

	@Test
	@DisplayName("Must save book")
	public void saveBookTest() {
		Book book = createNewBook();
		Book bookSaved = createNewBook();
		bookSaved.setId(0L);

		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
		Mockito.when(repository.save(book)).thenReturn(bookSaved);

		Book savedBook = service.save(book);

		assertThat(savedBook.getId()).isNotNull();
		assertThat(savedBook.getTitle()).isEqualTo("My book");
		assertThat(savedBook.getAuthor()).isEqualTo("Wleydson");
		assertThat(savedBook.getIsbn()).isEqualTo("123123");

	}
	
	@Test
	@DisplayName("throws error when saving a book with isbn that already exists")
	public void notSaveBookWithDuplicatedIsnb() {
		Book book = createNewBook();
		Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		
		Throwable exception = Assertions.catchThrowable(() -> service.save(book));
		
		assertThat(exception).isInstanceOf(BusinessException.class).hasMessage("isbn already registered");
		Mockito.verify(repository, Mockito.never()).save(book);
	}
	
	
	private Book createNewBook() {
		return Book.builder().title("My book").author("Wleydson").isbn("123123").build();
	}
	
}
