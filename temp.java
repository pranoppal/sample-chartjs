@Bean
public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
    FilterRegistrationBean<ForwardedHeaderFilter> filterRegBean = new FilterRegistrationBean<>();
    filterRegBean.setFilter(new ForwardedHeaderFilter());
    filterRegBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return filterRegBean;
}
