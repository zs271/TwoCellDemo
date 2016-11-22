package com.pervasid.rfid.experiment;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;


public class SendEmail {
	public static void main(String[] args) {

		final String username = "zsc33zsc@gmail.com";
		final String password = "zsc33zsc";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("zsc33zsc@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse("zsc33zsc@gmail.com"));
			message.setSubject("Testing Subject");
			message.setText("Dear Mail Crawler,"
				+ "\n\n This is Sicheng!");

			Transport.send(message);

			System.out.println("Message sent successfully");

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
	
}
