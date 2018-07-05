package sa.common.web;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sa.common.model.entity.User;
import sa.common.repository.UserRepository;

import java.io.IOException;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/avatars")
public class AvatarController {

    @Autowired
    UserRepository userRepository;


    @PostMapping("/{username}")
    public void updateAvatar(@RequestParam("file") MultipartFile file, @PathVariable("username") String username) {

        this.userRepository.findByUsername(username).ifPresent(user -> {
            try {
                user.setAvatar(file.getBytes());
            } catch (IOException e) {
                log.info(e);
            }
            userRepository.save(user);
        });
    }

    @GetMapping("/{username}")
    public Optional<byte[]> getAvatarForUser(@PathVariable("username") String username){
        return this.userRepository.findByUsername(username).map(User::getAvatar);
    }
}
