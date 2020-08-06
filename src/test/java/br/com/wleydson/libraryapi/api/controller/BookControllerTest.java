package br.com.wleydson.libraryapi.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.wleydson.libraryapi.api.dto.BookDTO;
import br.com.wleydson.libraryapi.exception.BusinessException;
import br.com.wleydson.libraryapi.model.entity.Book;
import br.com.wleydson.libraryapi.service.BookService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

	static String BOOK_API = "/api/books/";
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	BookService service;
	
	@Test
	@DisplayName("Must create a book successfully")
	public void createdBookTest() throws Exception{
		BookDTO dto = createNewBookDTO();
		Book saveBook = Book.builder().id(0L).title("My book").author("Wleydson").isbn("123123").build();
		
		BDDMockito.given(service.save(Mockito.any(Book.class ))).willReturn(saveBook);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
																.post(BOOK_API)
																.contentType(MediaType.APPLICATION_JSON)
																.accept(MediaType.APPLICATION_JSON)
																.content(json);
		
		mvc.perform(request)
			.andExpect( status().isCreated() )
			.andExpect( jsonPath("id").value(0L) )
			.andExpect( jsonPath("title").value(dto.getTitle()) )
			.andExpect( jsonPath("author").value(dto.getAuthor()) )
			.andExpect( jsonPath("isbn").value(dto.getIsbn()) );
	}
	
	@Test
	@DisplayName("You should throw an error when creating a book")
	public void createdInvaledBookTest() throws Exception{
		
		String json = new ObjectMapper().writeValueAsString(new BookDTO());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
																.post(BOOK_API)
																.contentType(MediaType.APPLICATION_JSON)
																.accept(MediaType.APPLICATION_JSON)
																.content(json);
		
		mvc.perform(request)
			.andExpect( status().isBadRequest() )
			.andExpect( jsonPath("errors", hasSize(3) ));
		
	}
	
	@Test
	@DisplayName("throws error when registering isbn that already exists")
	public void createdBookWithDuplicatedIsbnTest() throws Exception{
		BookDTO dto = createNewBookDTO();
		String json = new ObjectMapper().writeValueAsString(dto);
		String error = "isbn already registered";
		BDDMockito.given(service.save(Mockito.any(Book.class ))).willThrow(new BusinessException(error));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
																.post(BOOK_API)
																.contentType(MediaType.APPLICATION_JSON)
																.accept(MediaType.APPLICATION_JSON)
																.content(json);
		
		mvc.perform(request)
			.andExpect( status().isBadRequest() )
			.andExpect( jsonPath("errors", hasSize(1)) )
			.andExpect( jsonPath("errors[0]").value(error) );
	}
	
	@Test
	@DisplayName("get information from a book")
	public void getBookDetailsTest() throws Exception {
		Long id = 1L;
		Book book = createNewBook(id);
		
		BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
																	.get(BOOK_API.concat("/"+id))
																	.accept(MediaType.APPLICATION_JSON);
		
		mvc.perform(request)
			.andExpect( status().isOk() )
			.andExpect( jsonPath("id").value(id) )
			.andExpect( jsonPath("title").value(createNewBook().getTitle()) )
			.andExpect( jsonPath("author").value(createNewBook().getAuthor()) )
			.andExpect( jsonPath("isbn").value(createNewBook().getIsbn()) );
		
	}
	
	@Test
	@DisplayName("must launch not found when book does not exist")
	public void bookNotFoundTest() throws Exception {
		
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
																	.get(BOOK_API.concat("/"+1))
																	.accept(MediaType.APPLICATION_JSON);
		mvc.perform(request)
			.andExpect( status().isNotFound());
		
	}
	
	@Test
	@DisplayName("you must delete a book")
	public void deleteBookTest() throws Exception {
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn( Optional.of(Book.builder().id(1L).build()) );
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
																	.delete(BOOK_API.concat("/"+1));
		
		mvc.perform(request).andExpect( status().isNoContent());	
	}
	
	@Test
	@DisplayName("must launch not found when not finding a book to delete")
	public void deleteBookNotFoundTest() throws Exception {
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
																	.delete(BOOK_API.concat("/"+1));
		
		mvc.perform(request).andExpect( status().isNotFound());	
	}
	
	@Test
	@DisplayName("must update a book")
	public void updateBookTest() throws Exception {
		Long id = 1L;
		String json = new ObjectMapper().writeValueAsString( createNewBookDTO() );
		
		Book updatingBook = Book.builder().id(1L).title("Update").author("You").isbn("000").build();
		
		BDDMockito.given(service.getById(id)).willReturn( Optional.of(updatingBook) );
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
																.put( BOOK_API.concat("/"+id) )
																.contentType(MediaType.APPLICATION_JSON)
																.accept(MediaType.APPLICATION_JSON)
																.content(json);
		
		Book updatedBook = Book.builder().id(1L).title("My book").author("Wleydson").isbn("000").build();
		BDDMockito.given(service.update(updatingBook)).willReturn(updatedBook);

		
		mvc.perform(request)
					.andExpect( status().isOk() )
					.andExpect( jsonPath("id").value(id) )
					.andExpect( jsonPath("title").value(updatingBook.getTitle()) )
					.andExpect( jsonPath("author").value(updatingBook.getAuthor()) )
					.andExpect( jsonPath("isbn").value("000"));	
	}
	
	
	@Test
	@DisplayName("should launch 404 when updating book that does not exist")
	public void updateBookNotFoundTest() throws Exception {
		String json = new ObjectMapper().writeValueAsString( createNewBookDTO() );		
		BDDMockito.given(service.getById(Mockito.anyLong())).willReturn( Optional.empty() );
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
																.put( BOOK_API.concat("/"+1) )
																.contentType(MediaType.APPLICATION_JSON)
																.accept(MediaType.APPLICATION_JSON)
																.content(json);
		
		mvc.perform(request).andExpect( status().isNotFound() );
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	@DisplayName("search with filter")
	public void findBookTest() throws Exception {
		Book book = createNewBook(1L);
		
		BDDMockito.given( service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)) )
				.willReturn( new PageImpl( Arrays.asList(book), PageRequest.of(0,100), 1) );
		
		String queryString = String.format("?title=%s&author=%s&page=0&size=100",book.getTitle(), book.getAuthor());
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);
		
		 mvc
         .perform( request )
         .andExpect( status().isOk() )
         .andExpect( jsonPath("content", hasSize(1)))
         .andExpect( jsonPath("totalElements").value(1) )
         .andExpect( jsonPath("pageable.pageSize").value(100) )
         .andExpect( jsonPath("pageable.pageNumber").value(0));
		
	}
	
	
	private BookDTO createNewBookDTO() {
		return BookDTO.builder().title("My book").author("Wleydson").isbn("123123").build();
	}
	
	private Book createNewBook() {
		return Book.builder().title("My book").author("Wleydson").isbn("123123").build();
	}
	
	private Book createNewBook(final Long id) {
		return Book.builder().title("My book").author("Wleydson").isbn("123123").id(id).build();
	}
}
