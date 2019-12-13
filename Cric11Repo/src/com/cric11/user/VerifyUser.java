//$Id$
package com.cric11.user;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class VerifyUser {
	
	public static Boolean verifyUserEmail(Users userObj) {
		Boolean check = false;
		try {
		
		final String userName = "ajayprabbhu97@gmail.com";
        final String password = "chesslotuspetal";
		
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");  
        
        Session session = Session.getInstance(props, new Authenticator() {
        	@Override
        	protected PasswordAuthentication getPasswordAuthentication() {
        		return new PasswordAuthentication(userName, password);
        	}
		});
        
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("ajayprabbhu97@gmail.com"));
        message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(userObj.getEmailAddress())
        );
        message.setSubject("Email Verification");
        message.setText("Please click following link to verify your email\n"
        		+ "http://localhost:8081/cric11/auth/verifyemail?token="+userObj.getResetId());
        

        Transport.send(message);

        System.out.println("Done");
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}	
		
		return check;
	}

}
