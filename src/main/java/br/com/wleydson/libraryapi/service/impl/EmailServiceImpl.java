package br.com.wleydson.libraryapi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import br.com.wleydson.libraryapi.service.EmailService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

	@Value("${application.mail.default-remetent}")
    private String remetent;
	
	private final String SUBJECT = "Book with overdue loan";
	
	private final JavaMailSender javaMailSender;
		
	@Override
	public void sendMails(String message, List<String> mailsList) {
		String[] mails = mailsList.toArray(new String[mailsList.size()]);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(remetent);
        mailMessage.setSubject(SUBJECT);
        mailMessage.setText(message);
        mailMessage.setTo(mails);

        javaMailSender.send(mailMessage);
	}

}
