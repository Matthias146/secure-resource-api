package security.secure_resource_api.resource;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import security.secure_resource_api.user.User;
import security.secure_resource_api.user.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public ResourceService(
            ResourceRepository resourceRepository,
            UserRepository userRepository
    ) {
        this.resourceRepository = resourceRepository;
        this.userRepository = userRepository;
    }

    public List<Resource> findAllByOwner(Long ownerId) {
        return resourceRepository.findAllByOwnerId(ownerId);
    }

    public Resource findByIdAndOwner(Long resourceId, Long ownerId) {
        return resourceRepository.findByIdAndOwnerId(resourceId, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(resourceId));
    }

    @Transactional
    public Resource create(
            Long ownerId,
            String title,
            String content
    ) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + ownerId
                ));

        Instant now = Instant.now();

        Resource resource = new Resource(
                title,
                content,
                owner,
                now,
                now
        );

        return resourceRepository.save(resource);
    }

    @Transactional
    public Resource update(
            Long resourceId,
            Long ownerId,
            String title,
            String content
    ) {
        Resource resource = findByIdAndOwner(resourceId, ownerId);

        resource.update(
                title,
                content,
                Instant.now()
        );

        return resource;
    }

    @Transactional
    public void delete(Long resourceId, Long ownerId) {
        Resource resource = findByIdAndOwner(resourceId, ownerId);

        resourceRepository.delete(resource);
    }
}
