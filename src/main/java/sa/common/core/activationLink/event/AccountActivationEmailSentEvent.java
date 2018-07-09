package sa.common.core.activationLink.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountActivationEmailSentEvent {

    private final String linkId;
    private final String email;
}