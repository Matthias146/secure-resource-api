package security.secure_resource_api.resource;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public List<ResourceResponse> findAll(
            @RequestParam Long ownerId
    ) {
        return resourceService.findAllByOwner(ownerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResourceResponse findById(
            @PathVariable Long id,
            @RequestParam Long ownerId
    ) {
        return toResponse(
                resourceService.findByIdAndOwner(id, ownerId)
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResourceResponse create(
            @RequestParam Long ownerId,
            @Valid @RequestBody ResourceRequest request
    ) {
        Resource resource = resourceService.create(
                ownerId,
                request.title(),
                request.content()
        );

        return toResponse(resource);
    }

    @PutMapping("/{id}")
    public ResourceResponse update(
            @PathVariable Long id,
            @RequestParam Long ownerId,
            @Valid @RequestBody ResourceRequest request
    ) {
        Resource resource = resourceService.update(
                id,
                ownerId,
                request.title(),
                request.content()
        );

        return toResponse(resource);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            @RequestParam Long ownerId
    ) {
        resourceService.delete(id, ownerId);
    }

    private ResourceResponse toResponse(Resource resource) {
        return new ResourceResponse(
                resource.getId(),
                resource.getTitle(),
                resource.getContent(),
                resource.getOwner().getId(),
                resource.getCreatedAt(),
                resource.getUpdatedAt()
        );
    }
}
