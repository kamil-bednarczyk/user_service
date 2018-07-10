package sa.common.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import sa.common.integration.BaseIntegrationTest;
import sa.common.model.entity.User;
import sa.common.model.enums.Role;
import sa.common.repository.UserRepository;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OAuth2SecuredResourceAccessTest extends BaseIntegrationTest {

    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Value("${security.oauth2.client.client-id}")
    private String secret;
    @Value("${security.oauth2.client.client-secret}")
    private String clientId;

    private MockMvc mockMvc;
    private User user;

    @Before
    public void setup() {

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilter(springSecurityFilterChain).build();

        user = User.builder()
                .id(UUID.randomUUID().toString())
                .username("test")
                .password("test")
                .email("test@email.com")
                .enabled(true)
                .avatar(new byte[20])
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    @After
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    public void givenNoToken_whenGetSecureRequest_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void givenValidRole_whenGetSecureRequest_thenReturnStatusOk() throws Exception {
        String accessToken = obtainAccessToken(user.getUsername(), user.getPassword());

        mockMvc.perform(get("/users/" + user.getId())
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.enabled").value(user.isEnabled()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.role").value(user.getRole().toString()));
    }

    private String obtainAccessToken(String username, String password) throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", clientId);
        params.add("username", username);
        params.add("password", password);
        params.add("scope", "read write");

        ResultActions result
                = mockMvc.perform(post("/oauth/token")
                .params(params)
                .with(httpBasic(clientId, secret))
                .accept("application/json;charset=UTF-8"))
                .andExpect(status().isOk());

        String resultString = result.andReturn().getResponse().getContentAsString();
        JacksonJsonParser jsonParser = new JacksonJsonParser();
        return jsonParser.parseMap(resultString).get("access_token").toString();
    }
}
