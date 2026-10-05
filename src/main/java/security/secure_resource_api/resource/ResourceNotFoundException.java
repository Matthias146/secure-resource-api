package security.secure_resource_api.resource;

public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(Long resourceId) {
        super("Resource not found: " + resourceId);
    }
}
