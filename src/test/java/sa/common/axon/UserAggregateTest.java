package sa.common.axon;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;
import sa.common.core.user.*;
import sa.common.model.enums.Role;

public class UserAggregateTest {

    private FixtureConfiguration<UserAggragate> fixture;

    @Before
    public void setUp() {
        fixture = new AggregateTestFixture<>(UserAggragate.class);
    }

    @Test
    public void testCreateUser() {
        fixture.givenNoPriorActivity()
                .when(CreateUserCommand.builder()
                        .id("id")
                        .username("username")
                        .password("password")
                        .email("rmail@email.com")
                        .enabled(false)
                        .role(Role.USER)
                        .build())
                .expectEvents(UserCreatedEvent.builder()
                        .id("id")
                        .username("username")
                        .password("password")
                        .email("rmail@email.com")
                        .enabled(false)
                        .role(Role.USER)
                        .build());
    }

    @Test
    public void testUpdateUser() {
        fixture.given(CreateUserCommand.builder()
                .id("id")
                .username("username")
                .password("password")
                .email("rmail@email.com")
                .enabled(false)
                .role(Role.USER)
                .build())
                .when(UpdateUserCommand.builder()
                        .id("id")
                        .username("updated")
                        .email("updated@email.com")
                        .enable(false)
                        .role(Role.ADMIN.toString())
                        .build())
                .expectEvents(UserUpdatedEvent.builder()
                        .id("id")
                        .username("updated")
                        .email("updated@email.com")
                        .enable(false)
                        .role(Role.ADMIN.toString())
                        .build());

    }

}
