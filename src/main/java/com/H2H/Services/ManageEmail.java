package com.H2H.Services;


import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.H2H.Entity.ACKFILES;

@Service
public class ManageEmail {

	@Value("${app.emails}")
	private List<String> emailList;

	@Value("${app.sender.email}")	
	private String senderEmail;
	
//	@Value("${app.sender.password}")	
    private String senderPassword = "";
	

//	private final String body = "This is Test Email";
	
	public void manageEmail(ACKFILES vFileSC) {
	    String ackfileName = vFileSC.getFINAME();
	    
	    String body = "The ACK file ("+ ackfileName +") at " + new java.util.Date() + " has been received successfully.";
		boolean smtpAuth = true;
		try {
			Properties props = new Properties();
			props.put("mail.smtp.host", "192.168.234.51"); // host IP	
			props.put("mail.smtp.port", "25");
			props.put("mail.smtp.auth", "false");
			props.put("mail.smtp.starttls.enable", "false");
			props.put("mail.smtp.starttls.required", "false");
			props.put("mail.smtp.ssl.enable", "false");
			props.put("mail.smtp.ssl.trust", "*");
//			props.put("mail.debug", "true");
			
			 Session session;
	            if (smtpAuth) {
	                session = Session.getInstance(props, new Authenticator() {
	                    @Override
	                    protected PasswordAuthentication getPasswordAuthentication() {
	                        return new PasswordAuthentication(senderEmail, senderPassword);
	                    }
	                });
	            } else {
	                session = Session.getInstance(props);
	            }

			MimeMessage message = new MimeMessage(session);
			message.setFrom();

			message.setFrom(senderEmail);

			Address[] recipientAddresses = new Address[emailList.size()];

			for (int i = 0; i < emailList.size(); i++) {
                recipientAddresses[i] = new InternetAddress(emailList.get(i));
            }
			
			message.setRecipients(Message.RecipientType.TO, recipientAddresses);
			message.setSubject("ACK file Received from NPCI");
			message.setText(body);

			Transport.send(message);

		} catch (Exception e) {
			System.out.println("Error is >>>>>>>>>>>>>>>>>>>"+e.getMessage());
		}
	}
	
//	@Autowired
//    private JavaMailSender mailSender;
//
//    @Value("${app.sender.email}")
//    private String senderEmail;
//
//    @Value("${app.emails}")
//    private String emailListProperty;
//
//    public void manageEmail(ACKFILES ackFile) {
//        try {
//            String subject = "ACK file Received from NPCI";
//            String body = "The ACK file for " + new Date() + " has been received successfully.";
//            
//            List<String> emailList = Arrays.stream(emailListProperty.split(","))
//                    .map(String::trim)
//                    .filter(email -> !email.isEmpty())
//                    .collect(Collectors.toList());
//
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(senderEmail);
//            message.setTo(emailList.toArray(new String[0]));
//            message.setSubject(subject);
//            message.setText(body);
//            mailSender.send(message);
//            System.out.println("✅ Email sent successfully from " + senderEmail);
//
//        } catch (Exception e) {
//            System.err.println("❌ Failed to send email: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
	
}
