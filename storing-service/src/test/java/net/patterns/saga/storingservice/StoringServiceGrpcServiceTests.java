package net.patterns.saga.storingservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.patterns.saga.common.grpc.StoreRequest;
import net.patterns.saga.common.grpc.StoreResponse;
import net.patterns.saga.common.grpc.StoringServiceGrpc;
import net.patterns.saga.common.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigInteger;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext // Ensures that the grpc-server is properly shutdown after each test
class StoringServiceGrpcServiceTests {

    @GrpcClient("StoringService")
    StoringServiceGrpc.StoringServiceBlockingStub storingServiceStub;

    @Test
    @DirtiesContext
    void testStoreGrpc() {
        net.patterns.saga.common.grpc.Item item = net.patterns.saga.common.grpc.Item.newBuilder().setName("Test").setVendor("Vendor").setVersion("1.0").setPrice(1).build();

        StoreRequest request = StoreRequest.newBuilder()
                .setItem(item)
                .build();

        StoreResponse response = storingServiceStub.store(request);
        assertNotNull(response);
        assertEquals(1, response.getResults());
    }

}
