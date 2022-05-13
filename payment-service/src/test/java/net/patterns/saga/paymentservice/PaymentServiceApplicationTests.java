package net.patterns.saga.paymentservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.patterns.saga.common.model.order.OrderRequestDTO;
import net.patterns.saga.common.model.payment.PaymentRequestDTO;
import net.patterns.saga.paymentservice.support.BalanceNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PaymentServiceApplicationTests {

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @Test
    void balanceTest() throws Exception {
        PaymentRequestDTO payment = PaymentRequestDTO.builder()
                .orderId(UUID.randomUUID())
                .userId(-1)
                .amount(10000d)
                .build();

        mockMvc.perform(get("/payment-service/credit/-2")
                        .content(mapper.writeValueAsString(payment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(2000.0)));
    }

    @Test
    void paymentExceedExcTest() throws Exception {
        PaymentRequestDTO payment = PaymentRequestDTO.builder()
                .orderId(UUID.randomUUID())
                .userId(-1)
                .amount(10000d)
                .build();

        mockMvc.perform(post("/payment-service/debit")
                        .content(mapper.writeValueAsString(payment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("REJECTED")));
    }


    @Test
    void paymentNoUserExcTest() throws Exception {
        PaymentRequestDTO payment = PaymentRequestDTO.builder()
                .orderId(UUID.randomUUID())
                .userId(0)
                .amount(10000d)
                .build();

        NestedServletException thrown = Assertions.assertThrows(NestedServletException.class, () -> {
            mockMvc.perform(post("/payment-service/debit")
                    .content(mapper.writeValueAsString(payment))
                    .contentType(MediaType.APPLICATION_JSON));
        });
    }

    @Test
    void paymentServiceTest() throws Exception {
        PaymentRequestDTO payment = PaymentRequestDTO.builder()
                .orderId(UUID.randomUUID())
                .userId(-1)
                .amount(100d)
                .build();

        mockMvc.perform(post("/payment-service/debit")
                        .content(mapper.writeValueAsString(payment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));

        mockMvc.perform(get("/payment-service/credit/-1")
                        .content(mapper.writeValueAsString(payment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(900.0)));
    }

}
