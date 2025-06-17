<dependency>
    <groupId>com.azure.spring</groupId>
    <artifactId>azure-spring-boot-starter-active-directory</artifactId>
    <version>4.6.0</version> <!-- or the latest -->
</dependency>


  import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.*;
import java.io.IOException;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Component
public class GraphApiSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public GraphApiSuccessHandler(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;

            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName());

            String accessToken = client.getAccessToken().getTokenValue();

            // Call Microsoft Graph
            String url = "https://graph.microsoft.com/v1.0/me/memberOf?$select=id,displayName";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> graphResponse = restTemplate.exchange(
                    url, HttpMethod.GET, entity, String.class);

            // Do something with the response (log/store/process it)
            System.out.println("Graph Response: " + graphResponse.getBody());
        }

        // Redirect to home or where you want to land post-login
        response.sendRedirect("/"); // or any path you want
    }
}




import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private GraphApiSuccessHandler graphApiSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .anyRequest().authenticated()
            .and()
            .oauth2Login()
                .successHandler(graphApiSuccessHandler);
    }
}

  
