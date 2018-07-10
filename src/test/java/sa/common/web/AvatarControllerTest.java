package sa.common.web;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import sa.common.model.entity.User;
import sa.common.model.enums.Role;
import sa.common.repository.UserRepository;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AvatarControllerTest {

    private WebTestClient webTestClient;
    private UserRepository userRepository;
    private User user;
    private byte[] image;

    @Before
    public void setup() throws Exception {
        userRepository = mock(UserRepository.class);
        image = getTestImageAsByteArray();

        webTestClient = WebTestClient.bindToController(new AvatarController(userRepository))
                .configureClient()
                .baseUrl("/avatars/")
                .build();
    }

    @Test
    public void getAvatarForUser() {

        user = User.builder()
                .id("12345")
                .username("username")
                .password("password")
                .email("email")
                .role(Role.USER)
                .enabled(false)
                .avatar(image)
                .build();

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        webTestClient.get().uri(user.getUsername())
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
                .jsonPath("avatar", Arrays.toString(image));
    }

    private static byte[] getTestImageAsByteArray() throws Exception {
        BufferedImage bImage = ImageIO.read(new File("src/test/resources/test-image.png"));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(bImage, "png", bos);
        return bos.toByteArray();
    }
}