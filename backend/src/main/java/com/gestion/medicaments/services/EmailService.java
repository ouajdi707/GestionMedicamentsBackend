package com.gestion.medicaments.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String resetUrl) {
        try {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject("Réinitialisation de votre mot de passe");
                message.setText("Bonjour,\n\nVous avez demandé à réinitialiser votre mot de passe.\n\n" 
                        + "Veuillez cliquer sur le lien suivant pour définir un nouveau mot de passe:\n"
                        + resetUrl + "\n\n"
                        + "Ce lien est valable pendant 1 heure.\n\n"
                        + "Si vous n'avez pas demandé à réinitialiser votre mot de passe, veuillez ignorer cet e-mail.\n\n"
                        + "Cordialement,\nL'équipe Gestion Médicaments");
                
                mailSender.send(message);
                logger.info("Password reset email sent to: {}", to);
            } else {
                // For development: Just log the reset URL
                logger.info("Mail sender not configured, would have sent password reset email to: {}", to);
                logger.info("Reset URL: {}", resetUrl);
            }
        } catch (Exception e) {
            logger.error("Error sending password reset email: {}", e.getMessage());
        }
    }
} 