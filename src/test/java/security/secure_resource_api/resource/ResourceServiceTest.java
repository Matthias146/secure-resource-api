package security.secure_resource_api.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import security.secure_resource_api.user.Role;
import security.secure_resource_api.user.User;
import security.secure_resource_api.user.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private UserRepository userRepository;

    private ResourceService resourceService;

    @BeforeEach
    void setUp() {
        resourceService = new ResourceService(
                resourceRepository,
                userRepository
        );
    }

    @Test
    void shouldFindAllResourcesByOwner() {
        Long ownerId = 1L;

        User owner = new User(
                "owner@example.com",
                "hash",
                Role.USER
        );

        Resource resource = new Resource(
                "Title",
                "Content",
                owner,
                Instant.now(),
                Instant.now()
        );

        when(resourceRepository.findAllByOwnerId(ownerId))
                .thenReturn(List.of(resource));

        var result = resourceService.findAllByOwner(ownerId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getTitle()).isEqualTo("Title");

        verify(resourceRepository).findAllByOwnerId(ownerId);
    }

    @Test
    void shouldFindResourceByIdAndOwner() {
        Long resourceId = 1L;
        Long ownerId = 2L;

        User owner = new User(
                "owner@example.com",
                "hash",
                Role.USER
        );

        Resource resource = new Resource(
                "Secret",
                "Private content",
                owner,
                Instant.now(),
                Instant.now()
        );

        when(resourceRepository.findByIdAndOwnerId(resourceId, ownerId))
                .thenReturn(Optional.of(resource));

        Resource result = resourceService.findByIdAndOwner(
                resourceId,
                ownerId
        );

        assertThat(result).isSameAs(resource);

        verify(resourceRepository)
                .findByIdAndOwnerId(resourceId, ownerId);
    }

    @Test
    void shouldThrowWhenResourceDoesNotBelongToOwner() {
        Long resourceId = 1L;
        Long ownerId = 999L;

        when(resourceRepository.findByIdAndOwnerId(resourceId, ownerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                resourceService.findByIdAndOwner(resourceId, ownerId)
        )
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Resource not found: 1");

        verify(resourceRepository)
                .findByIdAndOwnerId(resourceId, ownerId);
    }

    @Test
    void shouldCreateResourceForExistingUser() {
        Long ownerId = 1L;

        User owner = new User(
                "owner@example.com",
                "hash",
                Role.USER
        );

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(owner));

        when(resourceRepository.save(any(Resource.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Resource result = resourceService.create(
                ownerId,
                "New Resource",
                "Content"
        );

        assertThat(result.getTitle()).isEqualTo("New Resource");
        assertThat(result.getContent()).isEqualTo("Content");
        assertThat(result.getOwner()).isSameAs(owner);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();

        verify(userRepository).findById(ownerId);
        verify(resourceRepository).save(any(Resource.class));
    }

    @Test
    void shouldThrowWhenCreatingResourceForMissingUser() {
        Long ownerId = 999L;

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                resourceService.create(
                        ownerId,
                        "Title",
                        "Content"
                )
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found: 999");

        verify(userRepository).findById(ownerId);
        verify(resourceRepository, never()).save(any());
    }

    @Test
    void shouldUpdateOwnedResource() {
        Long resourceId = 1L;
        Long ownerId = 2L;

        User owner = new User(
                "owner@example.com",
                "hash",
                Role.USER
        );

        Resource resource = new Resource(
                "Old title",
                "Old content",
                owner,
                Instant.now(),
                Instant.now()
        );

        when(resourceRepository.findByIdAndOwnerId(resourceId, ownerId))
                .thenReturn(Optional.of(resource));

        Resource result = resourceService.update(
                resourceId,
                ownerId,
                "New title",
                "New content"
        );

        assertThat(result.getTitle()).isEqualTo("New title");
        assertThat(result.getContent()).isEqualTo("New content");

        verify(resourceRepository)
                .findByIdAndOwnerId(resourceId, ownerId);
    }

    @Test
    void shouldNotUpdateForeignResource() {
        Long resourceId = 1L;
        Long ownerId = 999L;

        when(resourceRepository.findByIdAndOwnerId(resourceId, ownerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                resourceService.update(
                        resourceId,
                        ownerId,
                        "New title",
                        "New content"
                )
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verify(resourceRepository)
                .findByIdAndOwnerId(resourceId, ownerId);
    }

    @Test
    void shouldDeleteOwnedResource() {
        Long resourceId = 1L;
        Long ownerId = 2L;

        User owner = new User(
                "owner@example.com",
                "hash",
                Role.USER
        );

        Resource resource = new Resource(
                "Title",
                "Content",
                owner,
                Instant.now(),
                Instant.now()
        );

        when(resourceRepository.findByIdAndOwnerId(resourceId, ownerId))
                .thenReturn(Optional.of(resource));

        resourceService.delete(resourceId, ownerId);

        verify(resourceRepository)
                .findByIdAndOwnerId(resourceId, ownerId);

        verify(resourceRepository)
                .delete(resource);
    }

    @Test
    void shouldNotDeleteForeignResource() {
        Long resourceId = 1L;
        Long ownerId = 999L;

        when(resourceRepository.findByIdAndOwnerId(resourceId, ownerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                resourceService.delete(resourceId, ownerId)
        )
                .isInstanceOf(ResourceNotFoundException.class);

        verify(resourceRepository, never())
                .delete(any());
    }
}
