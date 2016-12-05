package com.pervasid.rfid.experiment;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;


public class EmailAlert {
	
	private final String username,password,smtpServer,sender,recepient;
	private Session session;
	
	
	public EmailAlert(final String username,final String password,String smtpServer,String sender,String recepient){
		this.username=username;
		this.password=password;
		this.smtpServer=smtpServer;
		this.recepient=recepient;
		this.sender=sender;
		
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", smtpServer);
		props.put("mail.smtp.port", "587");

		this.session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
		
	}
	
	public void sendEmail(String name,String subject,String emailMessage){
		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(recepient));
			message.setSubject(subject);
			message.setText("Dear "+name+",\n\n"
				+emailMessage);

			Transport.send(message);

			//System.out.println(emailMessage);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
		
	}
	public static void main(String[] args) {

		final String username = "pervasid99";
		final String password = "pervasid";
		String smtpServer="smtp.gmail.com";
		String sender="pervasid99@gmail.com";
		String recepient="zs271@cam.ac.uk";
		
		EmailAlert alert=new EmailAlert(username,password,smtpServer,sender,recepient);
		alert.sendEmail("Sicheng", "Hello", "Warning: a and b have been seperated");
		
		
	}	
}
