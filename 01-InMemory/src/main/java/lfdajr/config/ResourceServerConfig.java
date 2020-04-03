package lfdajr.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;


import java.io.IOException;
import java.security.KeyPair;

@Configuration
@EnableResourceServer
@EnableConfigurationProperties(SecurityProperties.class)
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	private static final String ROOT_PATTERN = "/**";

	private final SecurityProperties securityProperties;

	@Autowired
	private TokenStore tokenStore;

	public ResourceServerConfig(final SecurityProperties securityProperties) {
		this.securityProperties = securityProperties;
	}

	@Override
	public void configure(final ResourceServerSecurityConfigurer resources) {
		resources.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers(HttpMethod.GET, ROOT_PATTERN).access("#oauth2.hasScope('read')")
				.antMatchers(HttpMethod.POST, ROOT_PATTERN).access("#oauth2.hasScope('write')")
				.antMatchers(HttpMethod.PATCH, ROOT_PATTERN).access("#oauth2.hasScope('write')")
				.antMatchers(HttpMethod.PUT, ROOT_PATTERN).access("#oauth2.hasScope('write')")
				.antMatchers(HttpMethod.DELETE, ROOT_PATTERN).access("#oauth2.hasScope('write')");
	}

	/*private final TokenStore tokenStore;

	public ResourceServerConfig(final TokenStore tokenStore) {
		this.tokenStore = tokenStore;
	}

	@Override
	public void configure(final ResourceServerSecurityConfigurer resources) {
		resources.tokenStore(tokenStore);
	}*/

	/*@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri;

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
				.anyRequest().authenticated().and()
			.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
	}

	@Bean
	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
		//return NimbusJwtDecoder.withSecretKey("");
	}*/


}