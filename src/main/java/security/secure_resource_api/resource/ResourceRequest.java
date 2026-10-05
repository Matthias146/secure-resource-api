package security.secure_resource_api.resource;

import jakarta.validation.constraints.NotBlank;

public record ResourceRequest(

        @NotBlank
        String title,

        @NotBlank
        String content
) {}
