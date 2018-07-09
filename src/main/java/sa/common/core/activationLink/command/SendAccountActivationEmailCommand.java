package sa.common.core.activationLink.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

@AllArgsConstructor
@Getter
public class SendAccountActivationEmailCommand {
    @TargetAggregateIdentifier
    private final String linkId;

    private final String userId;
    private final String email;
}