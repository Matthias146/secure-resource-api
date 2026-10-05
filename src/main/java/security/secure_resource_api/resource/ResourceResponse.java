package security.secure_resource_api.resource;

import java.time.Instant;

public record ResourceResponse(
        Long id,
        String title,
        String content,
        Long ownerId,
        Instant createdAt,
        Instant updatedAt)
{}
