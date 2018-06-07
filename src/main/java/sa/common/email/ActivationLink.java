package sa.common.email;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;

@Data
@Builder
public class ActivationLink {

    @Id
    private String id;
    private String userId;
    private LocalDate expirationDate;
    private ActivationStatus status;
}
