package lfdajr.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
@Order(value = 4)
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

	@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http
				.authorizeRequests(authorizeRequests ->
						authorizeRequests
								.antMatchers(HttpMethod.GET, "/message/**").hasAuthority("SCOPE_message:read")
								.antMatchers(HttpMethod.POST, "/message/**").hasAuthority("SCOPE_message:write")
								.anyRequest().authenticated()
				)
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
		// @formatter:on
	}

	@Bean
	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
	}


}