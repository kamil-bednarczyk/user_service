package sa.common.core.activationLink.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@Getter
@AllArgsConstructor
public class CreateAccountActivationLinkCommand {

    @TargetAggregateIdentifier
    private final String linkId;
    private final String userId;
}