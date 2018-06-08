package sa.common.core.activationLink.event;

import lombok.Value;

@Value
public class AccountActivationEmailSentEvent {

    private final String linkId;
    private final String userId;
    private final String email;
}
