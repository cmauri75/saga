package net.patterns.saga.storingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.patterns.saga.common.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigInteger;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class StoringServiceApplicationTests {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testStore() throws Exception{
        Item i = Item.builder().name("Coca Cola").version("2.0").vendor("Tester").price(new BigInteger("10")).build();

        mockMvc.perform(MockMvcRequestBuilders.post("/storing-service/store")
                        //.header("clientSecret", clientSecret)
                        .content(mapper.writeValueAsString(i))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", is(1)));

        mockMvc.perform(MockMvcRequestBuilders.post("/storing-service/store")
                        .content(mapper.writeValueAsString(i))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", is(2)));

        mockMvc.perform(MockMvcRequestBuilders.get("/storing-service/store")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", is(2)));
    }

}
