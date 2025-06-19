@Service
public class GraphApiService {

    private final RestTemplate restTemplate;

    public GraphApiService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    public List<String> fetchAppRoles(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // You can change "me" to specific userId if needed
        ResponseEntity<Map> response = restTemplate.exchange(
            "https://graph.microsoft.com/v1.0/me/appRoleAssignments",
            HttpMethod.GET,
            requestEntity,
            Map.class
        );

        List<String> roleIds = new ArrayList<>();
        if (response.getStatusCode().is2xxSuccessful()) {
            List<Map<String, Object>> assignments = (List<Map<String, Object>>) response.getBody().get("value");

            for (Map<String, Object> assignment : assignments) {
                String roleId = (String) assignment.get("appRoleId");
                roleIds.add(roleId); // You can also map to display names if needed
            }
        }

        return roleIds;
    }
}



@Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

    String requestURI = request.getRequestURI();

    if (requestURI.contains("/swagger") || requestURI.contains("/swagger-ui")) {
        HttpSession session = request.getSession();
        @SuppressWarnings("unchecked")
        List<String> appRoles = (List<String>) session.getAttribute("appRoles");

        if (appRoles == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
                String clientRegistrationId = oauthToken.getAuthorizedClientRegistrationId();

                OAuth2AuthorizedClient authorizedClient =
                        authorizedClientService.loadAuthorizedClient(
                                clientRegistrationId,
                                oauthToken.getName()
                        );

                if (authorizedClient != null && authorizedClient.getAccessToken() != null) {
                    String accessToken = authorizedClient.getAccessToken().getTokenValue();
                    appRoles = graphApiService.fetchAppRoles(accessToken);
                    session.setAttribute("appRoles", appRoles);
                } else {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    return false;
                }
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
                return false;
            }
        }

        // Optional: check required role
        if (!appRoles.contains("YOUR_EXPECTED_ROLE_ID_OR_NAME")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return false;
        }
    }

    return true;
}
