package net.patterns.saga.inventoryservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.patterns.saga.common.model.inventory.InventoryRequestDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class InventoryServiceApplicationTests {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void stockTest() throws Exception {
        mockMvc.perform(get("/inventory-service/stock")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)));

        mockMvc.perform(get("/inventory-service/stock/-11")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId", is(-11)))
                .andExpect(jsonPath("$.quantity", is(5)));
    }

    @Test
    void stockEmptyTest() throws Exception {
        InventoryRequestDTO request = InventoryRequestDTO.builder()
                .userId(-1)
                .productId(-12)
                .orderId(UUID.randomUUID())
                .build();

        mockMvc.perform(post("/inventory-service/take")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("AVAILABLE")));

        mockMvc.perform(post("/inventory-service/take")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("UNAVAILABLE")));
    }


    @Test
    void stockNotExistentExcTest() {
        InventoryRequestDTO request = InventoryRequestDTO.builder()
                .userId(-1)
                .productId(-120)
                .orderId(UUID.randomUUID())
                .build();

        Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(post("/inventory-service/take")
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON));
        });
    }

    @Test
    void putStockTest() throws Exception {
        InventoryRequestDTO request = InventoryRequestDTO.builder()
                .userId(-1)
                .productId(-13)
                .orderId(UUID.randomUUID())
                .build();

        mockMvc.perform(get("/inventory-service/stock/-13")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(0)));

        mockMvc.perform(post("/inventory-service/put")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/inventory-service/stock/-13")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(1)));
    }

    @Test
    void takeStockAndInventoryTest() throws Exception {
        InventoryRequestDTO request = InventoryRequestDTO.builder()
                .userId(-1)
                .productId(-11)
                .orderId(UUID.randomUUID())
                .build();

        mockMvc.perform(get("/inventory-service/stock/-11")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(5)));

        mockMvc.perform(post("/inventory-service/take")
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("AVAILABLE")));

        mockMvc.perform(get("/inventory-service/stock/-11")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(4)));

        mockMvc.perform(get("/inventory-service/inventoryEvent")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }


}
