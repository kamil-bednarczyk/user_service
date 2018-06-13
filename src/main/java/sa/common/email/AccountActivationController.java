package sa.common.email;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sa.common.core.activationLink.command.ActivateAccountCommand;

import java.time.LocalDate;

@RestController
@RequestMapping("/users/activations")
public class AccountActivationController {

    private final CommandGateway commandGateway;

    public AccountActivationController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @GetMapping("/{id}")
    public void activate(@PathVariable("id") String id) {
        commandGateway.send(new ActivateAccountCommand(id, LocalDate.now()));
    }
}