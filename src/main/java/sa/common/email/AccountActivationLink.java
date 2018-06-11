package sa.common.email;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Builder
@Data
public class AccountActivationLink {

    @Id
    private String id;
    private String userId;
    private LocalDate expirationDate;
    private ActivationStatus status;
}