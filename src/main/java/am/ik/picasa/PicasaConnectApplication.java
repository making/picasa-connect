package am.ik.picasa;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
@EnableOAuth2Sso
public class PicasaConnectApplication extends WebSecurityConfigurerAdapter {

	@Configuration
	public static class WebConfig extends WebMvcConfigurerAdapter {
		@Override
		public void addViewControllers(ViewControllerRegistry registry) {
			registry.addViewController("/").setViewName("index");
		}
	}

	@Order(101)
	@Configuration
	public static class SecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		public void configure(WebSecurity web) throws Exception {
			web.ignoring().antMatchers(HttpMethod.GET, "/api/**");
		}
	}

	@Bean
	InitializingBean initRestTemplate(OAuth2RestTemplate restTemplate) {
		return () -> {
			restTemplate.getMessageConverters().add(0,
					new AtomFeedHttpMessageConverter());
			restTemplate.getInterceptors().add((request, body, execution) -> {
				request.getHeaders().add("GData-Version", "2");
				return execution.execute(request, body);
			});
		};
	}

	@Bean
	FilterRegistrationBean crosFilter() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setUrlPatterns(Collections.singleton("/api/*"));
		registrationBean.setFilter(new Filter() {
			@Override
			public void init(FilterConfig filterConfig) throws ServletException {
				// NO-OP
			}

			@Override
			public void doFilter(ServletRequest servletRequest,
					ServletResponse servletResponse, FilterChain filterChain)
							throws IOException, ServletException {
				HttpServletResponse response = (HttpServletResponse) servletResponse;
				response.setHeader("Access-Control-Allow-Origin", "*");
				response.setHeader("Access-Control-Allow-Methods",
						"POST, GET, OPTIONS, PUT, DELETE");
				response.setHeader("Access-Control-Max-Age", "3600");
				response.setHeader("Access-Control-Allow-Headers", "*");
				filterChain.doFilter(servletRequest, servletResponse);
			}

			@Override
			public void destroy() {
				// NO-OP
			}
		});
		return registrationBean;
	}

	@Profile("heroku")
	@Bean(name = "dataSource", destroyMethod = "close")
	DataSource dataSource() throws URISyntaxException {
		String databaseUrl = System.getenv("DATABASE_URL");
		URI dbUri = new URI(databaseUrl);
		String url = "jdbc:postgresql://" + dbUri.getHost() + ":" + dbUri.getPort()
				+ dbUri.getPath();
		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		DataSourceBuilder factory = DataSourceBuilder.create().url(url).username(username)
				.password(password);
		return factory.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(PicasaConnectApplication.class, args);
	}
}
