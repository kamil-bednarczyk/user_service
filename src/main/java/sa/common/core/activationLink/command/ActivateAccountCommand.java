package sa.common.core.activationLink.command;

import lombok.Value;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.time.LocalDate;

@Value
public class ActivateAccountCommand {

    @TargetAggregateIdentifier
    private final String linkId;
    private final LocalDate when;
}