package security.secure_resource_api.resource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import security.secure_resource_api.user.Role;
import security.secure_resource_api.user.User;
import security.secure_resource_api.user.UserRepository;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ResourceRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResourceRepository resourceRepository;

    @Test
    void shouldFindResourceByIdAndOwnerId() {
        User owner = new User(
                "owner@example.com",
                "hash",
                Role.USER
        );

        owner = userRepository.save(owner);

        Resource resource = new Resource(
                "Secret",
                "Private content",
                owner,
                Instant.now(),
                Instant.now()
        );

        resource = resourceRepository.save(resource);

        var result = resourceRepository.findByIdAndOwnerId(
                resource.getId(),
                owner.getId()
        );

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Secret");
    }

    @Test
    void shouldNotFindResourceWhenOwnerDoesNotMatch() {
        User owner = userRepository.save(
                new User("owner@example.com", "hash", Role.USER)
        );

        User otherUser = userRepository.save(
                new User("other@example.com", "hash", Role.USER)
        );

        Resource resource = resourceRepository.save(
                new Resource(
                        "Secret",
                        "Private content",
                        owner,
                        Instant.now(),
                        Instant.now()
                )
        );

        var result = resourceRepository.findByIdAndOwnerId(
                resource.getId(),
                otherUser.getId()
        );

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFindOnlyResourcesOwnedByUser() {
        User firstUser = userRepository.save(
                new User("first@example.com", "hash", Role.USER)
        );

        User secondUser = userRepository.save(
                new User("second@example.com", "hash", Role.USER)
        );

        resourceRepository.save(
                new Resource(
                        "First",
                        "Content",
                        firstUser,
                        Instant.now(),
                        Instant.now()
                )
        );

        resourceRepository.save(
                new Resource(
                        "Second",
                        "Content",
                        secondUser,
                        Instant.now(),
                        Instant.now()
                )
        );

        var result = resourceRepository.findAllByOwnerId(firstUser.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("First");
    }
}