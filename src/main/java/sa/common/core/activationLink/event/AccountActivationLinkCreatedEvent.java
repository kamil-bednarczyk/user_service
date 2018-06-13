package sa.common.core.activationLink.event;

import lombok.Value;

import java.time.LocalDate;

@Value
public class AccountActivationLinkCreatedEvent {

    private final String linkId;
    private final String userId;
    private final LocalDate expirationDate;

}
