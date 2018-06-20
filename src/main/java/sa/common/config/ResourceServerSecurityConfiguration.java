package sa.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

@Configuration
@EnableResourceServer
public class ResourceServerSecurityConfiguration extends ResourceServerConfigurerAdapter {

    private final static String WRITE_SCOPE = "#oauth2.hasScope('write')";
    private final static String READ_SCOPE = "#oauth2.hasScope('read')";

    @Override
    public void configure(HttpSecurity http) throws Exception {

        http.requestMatchers()
                .and().authorizeRequests()
                .antMatchers(HttpMethod.GET).access(READ_SCOPE)
                .antMatchers(HttpMethod.GET).access(WRITE_SCOPE)
                .antMatchers(HttpMethod.POST).access(WRITE_SCOPE)
                .antMatchers(HttpMethod.PUT).access(WRITE_SCOPE)
                .antMatchers(HttpMethod.DELETE).access(WRITE_SCOPE);
    }
}
