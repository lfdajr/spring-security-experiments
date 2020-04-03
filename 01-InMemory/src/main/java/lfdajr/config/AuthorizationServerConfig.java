package lfdajr.config;


import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import javax.sql.DataSource;
import java.security.KeyPair;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	static final String CLIENT_ID = "lfdajr";
	static final String CLIENT_SECRET = "$2a$10$UhR27klNkGQjDuRj3KzXyeqfnmCbAY8arWk9cBsT.8RxyHCrmhRW."; //"lfdajr-pass";
	static final String GRANT_TYPE_PASSWORD = "password";
	static final String AUTHORIZATION_CODE = "authorization_code";
	static final String REFRESH_TOKEN = "refresh_token";
	static final String CLIENT_CREDENTIALS = "client_credentials";
	static final String IMPLICIT = "implicit";
	static final String SCOPE_READ = "read";
	static final String SCOPE_WRITE = "write";
    static final String TRUST = "trust";
	static final int ACCESS_TOKEN_VALIDITY_SECONDS = 1*60*60;
    static final int FREFRESH_TOKEN_VALIDITY_SECONDS = 6*60*60;

	private AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;
	private final SecurityProperties securityProperties;
	private final UserDetailsService userDetailsService;

	public AuthorizationServerConfig(final PasswordEncoder passwordEncoder,
											final AuthenticationManager authenticationManager, final SecurityProperties securityProperties,
											final UserDetailsService userDetailsService) {
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.securityProperties = securityProperties;
		this.userDetailsService = userDetailsService;
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {

		configurer
				.inMemory()
				.withClient(CLIENT_ID)
				.secret(CLIENT_SECRET)
				.authorizedGrantTypes(GRANT_TYPE_PASSWORD, AUTHORIZATION_CODE, REFRESH_TOKEN, IMPLICIT, CLIENT_CREDENTIALS )
				.scopes(SCOPE_READ, SCOPE_WRITE, TRUST)
				.accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS).
				refreshTokenValiditySeconds(FREFRESH_TOKEN_VALIDITY_SECONDS);
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public DefaultTokenServices tokenServices(final TokenStore tokenStore,
											  final ClientDetailsService clientDetailsService) {
		DefaultTokenServices tokenServices = new DefaultTokenServices();
		tokenServices.setSupportRefreshToken(true);
		tokenServices.setTokenStore(tokenStore);
		tokenServices.setClientDetailsService(clientDetailsService);
		tokenServices.setAuthenticationManager(this.authenticationManager);
		return tokenServices;
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();

		SecurityProperties.JwtProperties jwtProperties = securityProperties.getJwt();
		KeyPair keyPair = keyPair(jwtProperties, keyStoreKeyFactory(jwtProperties));
		converter.setKeyPair(keyPair);
		return converter;
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore())
				.authenticationManager(authenticationManager)
				.userDetailsService(this.userDetailsService)
				.accessTokenConverter(accessTokenConverter());
	}

	private KeyPair keyPair(SecurityProperties.JwtProperties jwtProperties, KeyStoreKeyFactory keyStoreKeyFactory) {
		return keyStoreKeyFactory.getKeyPair(jwtProperties.getKeyPairAlias(), jwtProperties.getKeyPairPassword().toCharArray());
	}

	private KeyStoreKeyFactory keyStoreKeyFactory(SecurityProperties.JwtProperties jwtProperties) {
		return new KeyStoreKeyFactory(jwtProperties.getKeyStore(), jwtProperties.getKeyStorePassword().toCharArray());
	}
}