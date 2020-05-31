package br.com.wleydson.libraryapi.api.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.wleydson.libraryapi.api.dto.BookDTO;
import br.com.wleydson.libraryapi.service.BookService;
import br.com.wleydson.libraryapi.model.entity.Book;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
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
		BookDTO dto = BookDTO.builder().title("My book").author("Wleydson").isbn("123123").build();
		Book saveBook = Book.builder().id(0L).title("My book").author("Wleydson").isbn("123123").build();
		
		BDDMockito.given(service.save(Mockito.any(Book.class ))).willReturn(saveBook);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
																	.post(BOOK_API)
																	.contentType(MediaType.APPLICATION_JSON)
																	.accept(MediaType.APPLICATION_JSON)
																	.content(json);
		
		mvc
			.perform(request)
			.andExpect( status().isCreated() )
			.andExpect( jsonPath("id").value(0L) )
			.andExpect( jsonPath("title").value(dto.getTitle()) )
			.andExpect( jsonPath("author").value(dto.getAuthor()) )
			.andExpect( jsonPath("isbn").value(dto.getIsbn()) );
	}
	
	@Test
	@DisplayName("You should throw an error when creating a book")
	public void createdInvaledBookTest(){
		
	}
}
