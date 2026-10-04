package security.secure_resource_api.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        User user = userRepository.save(
                new User(
                        "user@example.com",
                        "hash",
                        Role.USER
                )
        );

        var result = userRepository.findByEmail("user@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        var result = userRepository.findByEmail("missing@example.com");

        assertThat(result).isEmpty();
    }
}