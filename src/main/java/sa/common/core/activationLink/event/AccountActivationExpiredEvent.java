package sa.common.core.activationLink.event;

import lombok.Value;

@Value
public class AccountActivationExpiredEvent {

    private final String linkId;
}
