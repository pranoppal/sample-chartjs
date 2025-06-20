@Component
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final GraphApiService graphApiService;

    public CustomOAuth2UserService(GraphApiService graphApiService) {
        this.graphApiService = graphApiService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String accessToken = userRequest.getAccessToken().getTokenValue();
        List<String> appRoles = graphApiService.fetchAppRoles(accessToken);

        // Convert to Spring authorities
        List<GrantedAuthority> authorities = appRoles.stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // or whatever prefix you use
            .collect(Collectors.toList());

        // Merge with existing authorities if needed
        authorities.addAll(oauth2User.getAuthorities());

        return new DefaultOAuth2User(
            authorities,
            oauth2User.getAttributes(),
            "name" // or whatever is your username attribute
        );
    }
}




@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .oauth2Login(oauth2 -> oauth2
            .userInfoEndpoint(userInfo -> userInfo
                .userService(customOAuth2UserService) // inject here
            )
        );
    return http.build();
}
