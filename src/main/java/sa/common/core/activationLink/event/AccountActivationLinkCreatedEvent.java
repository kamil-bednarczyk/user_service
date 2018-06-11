package sa.common.core.activationLink.event;

import lombok.Value;

@Value
public class AccountActivationLinkCreatedEvent {

    private final String linkId;
    private final String userId;

}
