package sa.common.core.activationLink.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountActivationExpiredEvent {

    private final String linkId;
}
