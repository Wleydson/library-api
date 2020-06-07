package br.com.wleydson.libraryapi.exception;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BusinessException(String e) {
		super(e);
	}

}
