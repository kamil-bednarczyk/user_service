package sa.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class JavaMailerConfiguration {

    private final String username;
    private final String password;
    private final Integer port;
    private final String host;
    private final Boolean sslEnabled;

    public JavaMailerConfiguration(@Value("${mail.username}") String username,
                                   @Value("${mail.password}") String password,
                                   @Value("${mail.port}") Integer port,
                                   @Value("${mail.host}") String host,
                                   @Value("${mail.smtp.ssl.enable}") Boolean sslEnabled) {
        this.username = username;
        this.password = password;
        this.port = port;
        this.host = host;
        this.sslEnabled = sslEnabled;
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setUsername(this.username);
        mailSender.setPassword(this.password);
        mailSender.setHost(this.host);
        mailSender.setPort(this.port);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.ssl.enable", this.sslEnabled); //
        props.put("mail.debug", true);
        props.put("mail.smtp.connectiontimeout", 15000);
        props.put("mail.smtp.timeout", 15000);
        props.put("mail.smtp.writetimeout", 15000);
        return mailSender;
    }
}