import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.IOException;

@Bean
public AuthenticationSuccessHandler graphSuccessHandler(OAuth2AuthorizedClientService authorizedClientService) {
    return (HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

            if (client != null) {
                OAuth2AccessToken accessToken = client.getAccessToken();
                String tokenValue = accessToken.getTokenValue();

                // Call Graph API
                String url = "https://graph.microsoft.com/v1.0/me/memberOf?$select=id,displayName";
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(tokenValue);
                headers.setAccept(List.of(MediaType.APPLICATION_JSON));

                HttpEntity<Void> entity = new HttpEntity<>(headers);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> graphResponse = restTemplate.exchange(
                        url, HttpMethod.GET, entity, String.class);

                System.out.println("Graph response: " + graphResponse.getBody());

                // TODO: you can check if user is in a specific group here
                // and redirect based on that
            }
        }

        // Redirect after success
        response.sendRedirect("/");
    };
}




import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthenticationSuccessHandler graphSuccessHandler) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth -> oauth
                .successHandler(graphSuccessHandler)
            );

        return http.build();
    }
}
