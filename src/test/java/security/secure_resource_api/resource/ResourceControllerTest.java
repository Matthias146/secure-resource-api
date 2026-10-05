package security.secure_resource_api.resource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import security.secure_resource_api.user.Role;
import security.secure_resource_api.user.User;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ResourceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ResourceService resourceService;

    @Test
    void shouldFindAllResourcesByOwner() throws Exception {
        User owner = new User(
                "owner@example.com",
                "hash",
                Role.USER
        );

        Resource resource = new Resource(
                "Test Resource",
                "Test Content",
                owner,
                Instant.parse("2026-10-05T10:00:00Z"),
                Instant.parse("2026-10-05T10:00:00Z")
        );

        when(resourceService.findAllByOwner(1L))
                .thenReturn(List.of(resource));

        mockMvc.perform(
                        get("/api/resources")
                                .param("ownerId", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Resource"))
                .andExpect(jsonPath("$[0].content").value("Test Content"));

        verify(resourceService).findAllByOwner(1L);
    }

    @Test
    void shouldFindResourceByIdAndOwner() throws Exception {
        User owner = new User(
                "owner@example.com",
                "hash",
                Role.USER
        );

        Resource resource = new Resource(
                "Secret",
                "Private Content",
                owner,
                Instant.now(),
                Instant.now()
        );

        when(resourceService.findByIdAndOwner(10L, 1L))
                .thenReturn(resource);

        mockMvc.perform(
                        get("/api/resources/10")
                                .param("ownerId", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Secret"))
                .andExpect(jsonPath("$.content").value("Private Content"));

        verify(resourceService).findByIdAndOwner(10L, 1L);
    }

    @Test
    void shouldCreateResource() throws Exception {
        User owner = new User(
                "owner@example.com",
                "hash",
                Role.USER
        );

        Resource resource = new Resource(
                "New Resource",
                "New Content",
                owner,
                Instant.now(),
                Instant.now()
        );

        when(resourceService.create(
                1L,
                "New Resource",
                "New Content"
        )).thenReturn(resource);

        mockMvc.perform(
                        post("/api/resources")
                                .param("ownerId", "1")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "title": "New Resource",
                                          "content": "New Content"
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("New Resource"))
                .andExpect(jsonPath("$.content").value("New Content"));

        verify(resourceService).create(
                1L,
                "New Resource",
                "New Content"
        );
    }

    @Test
    void shouldRejectCreateWhenTitleIsBlank() throws Exception {
        mockMvc.perform(
                        post("/api/resources")
                                .param("ownerId", "1")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "title": "",
                                          "content": "Content"
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest());

        verify(resourceService, never())
                .create(anyLong(), anyString(), anyString());
    }

    @Test
    void shouldUpdateResource() throws Exception {
        User owner = new User(
                "owner@example.com",
                "hash",
                Role.USER
        );

        Resource resource = new Resource(
                "Updated Title",
                "Updated Content",
                owner,
                Instant.now(),
                Instant.now()
        );

        when(resourceService.update(
                10L,
                1L,
                "Updated Title",
                "Updated Content"
        )).thenReturn(resource);

        mockMvc.perform(
                        put("/api/resources/10")
                                .param("ownerId", "1")
                                .contentType("application/json")
                                .content("""
                                        {
                                          "title": "Updated Title",
                                          "content": "Updated Content"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.content").value("Updated Content"));

        verify(resourceService).update(
                10L,
                1L,
                "Updated Title",
                "Updated Content"
        );
    }

    @Test
    void shouldDeleteResource() throws Exception {
        mockMvc.perform(
                        delete("/api/resources/10")
                                .param("ownerId", "1")
                )
                .andExpect(status().isNoContent());

        verify(resourceService).delete(10L, 1L);
    }
}
