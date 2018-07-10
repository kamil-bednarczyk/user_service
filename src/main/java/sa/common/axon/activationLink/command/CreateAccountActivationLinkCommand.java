package sa.common.axon.activationLink.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Getter
@AllArgsConstructor
public class CreateAccountActivationLinkCommand {

    @TargetAggregateIdentifier
    private final String linkId;
    private final String userId;
}