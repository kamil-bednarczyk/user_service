package sa.common.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Builder
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class AccountActivationLink {

    @Id
    private String id;
    private String userId;
    private LocalDate expirationDate;
    private ActivationStatus status;
}