package sa.common.email;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class AccountActivationLink {

    private String id;
    private String userId;
    private LocalDate expirationDate;
    private ActivationStatus status;
}
