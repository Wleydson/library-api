package br.com.wleydson.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

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
	
	@Test
	@DisplayName("fetch a book by id")
	public void bookFindByIdTest() {
		Long id = 1L;
		Book book = createNewBook();
		book.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
		
		Optional<Book> foundBook = repository.findById(id);
		
		
		assertThat(foundBook.isPresent()).isTrue();
		assertThat( foundBook.get().getId() ).isEqualTo(id);
		assertThat( foundBook.get().getAuthor() ).isEqualTo( book.getAuthor() );
		assertThat( foundBook.get().getTitle() ).isEqualTo( book.getTitle() );
		assertThat( foundBook.get().getIsbn() ).isEqualTo( book.getIsbn() );
	}
	
	@Test
	@DisplayName("error when not finding a book")
	public void bookNotFoundIdTest() {
		Long id = 1L;		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		Optional<Book> foundBook = repository.findById(id);
		
		assertThat(foundBook.isPresent()).isFalse();
	}
	
	@Test
	@DisplayName("delete book")
	public void deleteBookTest() {
		Book book = Book.builder().id(1L).build();
		
		org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.delete(book));
		
		Mockito.verify(repository, Mockito.times(1)).delete(book);
	}	
	
	@Test
	@DisplayName("delete invalid book")
	public void deleteInvalidBookTest() {
		Book invalidBook = new Book();
		
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.delete(invalidBook) );
		
		Mockito.verify(repository, Mockito.never() ).delete(invalidBook);
	}	
	
	@Test
	@DisplayName("update invalid book")
	public void updateInvalidBookTest() {
		Book invalidBook = new Book();
		
		org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> service.update(invalidBook) );
		
		Mockito.verify(repository, Mockito.never() ).save(invalidBook);
	}	
	
	@Test
	@DisplayName("update book")
	public void updateBookTest() {
		long id = 1L;
		Book updatingBook = Book.builder().id(id).build();
		
		Book updatedBook = createNewBook();
		updatedBook.setId(id);
		Mockito.when(repository.save(updatingBook)).thenReturn(updatedBook);
		
		Book book = service.update(updatingBook);
		
		assertThat( book.getId() ).isEqualTo( updatedBook.getId() );
		assertThat( book.getAuthor() ).isEqualTo( updatedBook.getAuthor() );
		assertThat( book.getTitle() ).isEqualTo( updatedBook.getTitle() );
		assertThat( book.getIsbn() ).isEqualTo( updatedBook.getIsbn() );
	}	
	
	private Book createNewBook() {
		return Book.builder().title("My book").author("Wleydson").isbn("123123").build();
	}
	
}
