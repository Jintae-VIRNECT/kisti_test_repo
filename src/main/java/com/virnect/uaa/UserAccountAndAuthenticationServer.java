package com.virnect.uaa;

import com.virnect.uaa.domain.auth.security.rememberme.RememberMeCookieProperty;
import com.virnect.uaa.domain.auth.security.session.SessionCookieProperty;
import com.virnect.uaa.domain.auth.security.token.TokenProperty;
import org.apache.tomcat.util.http.LegacyCookieProcessor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableConfigurationProperties({TokenProperty.class, SessionCookieProperty.class, RememberMeCookieProperty.class})
public class UserAccountAndAuthenticationServer {

    public static void main(String[] args) {
        SpringApplication.run(UserAccountAndAuthenticationServer.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
        return (factory -> factory.addContextCustomizers(
                (context -> context.setCookieProcessor(new LegacyCookieProcessor()))));
    }
}
