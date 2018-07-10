package sa.common.axon.activationLink.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class ActivateAccountCommand {

    @TargetAggregateIdentifier
    private final String linkId;
    private final LocalDate when;
}