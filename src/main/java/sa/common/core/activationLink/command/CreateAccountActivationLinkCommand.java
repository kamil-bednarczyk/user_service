package sa.common.core.activationLink.command;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class CreateAccountActivationLinkCommand {

    @TargetAggregateIdentifier
    private final String linkId;
    private final String userId;
}