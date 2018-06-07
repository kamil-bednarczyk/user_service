package sa.common.email;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sa.common.repository.UserRepository;

@RestController
@RequestMapping("/activations")
public class ActivationLinksController {

    private final ActivationLinkRepository activationLinkRepository;
    private final UserRepository userRepository;

    public ActivationLinksController(ActivationLinkRepository activationLinkRepository, UserRepository userRepository) {
        this.activationLinkRepository = activationLinkRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public void activate(@PathVariable("id") String id) {
        activationLinkRepository.findById(id).ifPresent(link ->
                userRepository.findById(link.getUserId()).ifPresent(user -> {
                            user.setEnabled(true);
                            userRepository.save(user);
                        }
                )
        );
    }
}
