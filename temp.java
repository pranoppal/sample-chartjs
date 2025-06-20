@Component("roleChecker")
public class RoleChecker {

    private final SecurityProperties securityProperties;

    public RoleChecker(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    public boolean hasAppRole(Authentication authentication) {
        String requiredRole = "ROLE_" + securityProperties.getApprole();
        return authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals(requiredRole));
    }
}



@PreAuthorize("@roleChecker.hasAppRole(authentication)")
@GetMapping("/api/secured")
public ResponseEntity<String> securedEndpoint() {
    return ResponseEntity.ok("Access granted");
}


@Component
@ConfigurationProperties(prefix = "my.security")
public class SecurityProperties {
    private String approle;

    public String getApprole() {
        return approle;
    }

    public void setApprole(String approle) {
        this.approle = approle;
    }
}
