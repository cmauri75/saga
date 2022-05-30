package net.patterns.saga.orchestratorservice;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import net.patterns.saga.common.model.orchestrator.OrchestratorRequestDTO;
import net.patterns.saga.common.model.order.OrderStatus;
import net.patterns.saga.orchestratorservice.service.OrchestratorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@WireMockTest(httpPort = 18080)
class OrchestrationServiceApplicationTests {

    @Autowired
    OrchestratorService orchestratorService;

    @Test
    void okOrderServiceTest() {
        String json = """
                {
                  "orderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                  "userId": -1,
                  "productId": -1,
                  "status": "AVAILABLE"
                }""";
        stubFor(WireMock.post("/take").willReturn(okJson(json)));

        String json2 = """
                {
                    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                        "userId": 0,
                        "status": "APPROVED",
                        "amount": 0
                }""";
        stubFor(WireMock.post("/debit").willReturn(okJson(json2)));

        OrchestratorRequestDTO order = OrchestratorRequestDTO.builder()
                .orderId(UUID.randomUUID())
                .userId(-1)
                .productId(-1)
                .build();

        var res = orchestratorService.orderProduct(order);
        assertEquals(OrderStatus.COMPLETED, res.getStatus());

    }

    @Test
    void noMoneyOrderServiceTest() {
        String json = """
                {
                  "orderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                  "userId": -1,
                  "productId": -1,
                  "status": "AVAILABLE"
                }""";
        stubFor(WireMock.post("/take").willReturn(okJson(json)));

        String json2 = """
                {
                    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                        "userId": 0,
                        "status": "REJECTED",
                        "amount": 0
                }""";
        stubFor(WireMock.post("/debit").willReturn(okJson(json2)));

        stubFor(WireMock.post("/put").willReturn(ok()));

        OrchestratorRequestDTO order = OrchestratorRequestDTO.builder()
                .orderId(UUID.randomUUID())
                .userId(-1)
                .productId(-1)
                .build();

        var res = orchestratorService.orderProduct(order);
        assertEquals(OrderStatus.CANCELLED, res.getStatus());

        verify(postRequestedFor(urlEqualTo("/put")));

        List<LoggedRequest> unmatched = WireMock.findUnmatchedRequests();
        assertEquals(0, unmatched.size());
    }

    @Test
    void noStockOrderServiceTest() {
        String json = """
                {
                  "orderId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                  "userId": -1,
                  "productId": -1,
                  "status": "UNAVAILABLE"
                }""";
        stubFor(WireMock.post("/take").willReturn(okJson(json)));

        String json2 = """
                {
                    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
                        "userId": 0,
                        "status": "APPROVED",
                        "amount": 0
                }""";
        stubFor(WireMock.post("/debit").willReturn(okJson(json2)));

        stubFor(WireMock.post("/credit").willReturn(ok()));

        OrchestratorRequestDTO order = OrchestratorRequestDTO.builder()
                .orderId(UUID.randomUUID())
                .userId(-1)
                .productId(-1)
                .build();

        var res = orchestratorService.orderProduct(order);
        assertEquals(OrderStatus.CANCELLED, res.getStatus());

        verify(postRequestedFor(urlEqualTo("/credit")));

        List<LoggedRequest> unmatched = WireMock.findUnmatchedRequests();
        assertEquals(0, unmatched.size());
    }


}
