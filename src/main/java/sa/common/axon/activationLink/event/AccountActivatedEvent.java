package sa.common.axon.activationLink.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AccountActivatedEvent {
    private final String linkId;
}