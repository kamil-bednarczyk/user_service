package sa.common.axon.activationLink.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class AccountActivationLinkCreatedEvent {

    private final String linkId;
    private final String userId;
    private final LocalDate expirationDate;
}
