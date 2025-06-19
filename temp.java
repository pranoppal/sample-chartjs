@Component
public class SwaggerOnlyApiInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        if (uri.equals("/api/special-endpoint")) {
            String referer = request.getHeader("Referer");

            if (referer == null || !referer.contains("/swagger-ui")) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "API accessible only via Swagger UI");
                return false;
            }
        }

        return true;
    }
}
