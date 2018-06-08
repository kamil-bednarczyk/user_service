package sa.common.core.activationLink.event;

import lombok.Value;

@Value
public class AccountActivationLinkCreatedEvent {

    private final String userId;
    private final String linkId;

}
