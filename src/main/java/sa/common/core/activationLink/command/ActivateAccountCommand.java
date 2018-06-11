package sa.common.core.activationLink.command;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Value
public class ActivateAccountCommand {

    @TargetAggregateIdentifier
    private final String linkId;
}