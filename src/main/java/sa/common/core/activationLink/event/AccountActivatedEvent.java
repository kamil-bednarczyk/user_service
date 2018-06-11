package sa.common.core.activationLink.event;

import lombok.Value;

@Value
public class AccountActivatedEvent {

    private final String linkId;
    private final String userId;
}