package br.com.challenge.weighing_management_service.controller;

import br.com.challenge.weighing_management_service.dto.TransportTransactionRequestDto;
import br.com.challenge.weighing_management_service.dto.TransportTransactionResponseDto;
import br.com.challenge.weighing_management_service.service.TransportTransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = TransportTransactionController.class)
@org.springframework.test.context.ActiveProfiles("test")
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class TransportTransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransportTransactionService transactionService;

    @Test
    void shouldCreateTransaction() throws Exception {
        TransportTransactionRequestDto request = new TransportTransactionRequestDto();
        request.setTruckId(1L);
        request.setGrainTypeId(1L);
        request.setBranchId(1L);

        TransportTransactionResponseDto response = new TransportTransactionResponseDto();
        response.setId(1L);
        response.setStatus("IN_PROGRESS");

        when(transactionService.create(any(TransportTransactionRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/transactions")
                        
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldSearchAllTransactions() throws Exception {
        TransportTransactionResponseDto transaction1 = new TransportTransactionResponseDto();
        transaction1.setId(1L);
        transaction1.setStatus("IN_PROGRESS");

        TransportTransactionResponseDto transaction2 = new TransportTransactionResponseDto();
        transaction2.setId(2L);
        transaction2.setStatus("COMPLETED");

        List<TransportTransactionResponseDto> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionService.search(null, null, null, null)).thenReturn(transactions);

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.data[1].status").value("COMPLETED"));
    }

    @Test
    void shouldSearchTransactionById() throws Exception {
        TransportTransactionResponseDto transaction = new TransportTransactionResponseDto();
        transaction.setId(1L);
        transaction.setStatus("IN_PROGRESS");

        when(transactionService.search(1L, null, null, null)).thenReturn(List.of(transaction));

        mockMvc.perform(get("/transactions")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void shouldSearchTransactionsByTruckId() throws Exception {
        TransportTransactionResponseDto transaction = new TransportTransactionResponseDto();
        transaction.setId(1L);
        transaction.setStatus("IN_PROGRESS");

        when(transactionService.search(null, 1L, null, null)).thenReturn(List.of(transaction));

        mockMvc.perform(get("/transactions")
                        .param("truckId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void shouldSearchTransactionsByBranchId() throws Exception {
        TransportTransactionResponseDto transaction = new TransportTransactionResponseDto();
        transaction.setId(1L);
        transaction.setStatus("IN_PROGRESS");

        when(transactionService.search(null, null, 1L, null)).thenReturn(List.of(transaction));

        mockMvc.perform(get("/transactions")
                        .param("branchId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void shouldSearchTransactionsByStatus() throws Exception {
        TransportTransactionResponseDto transaction = new TransportTransactionResponseDto();
        transaction.setId(1L);
        transaction.setStatus("IN_PROGRESS");

        when(transactionService.search(null, null, null, "IN_PROGRESS")).thenReturn(List.of(transaction));

        mockMvc.perform(get("/transactions")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].status").value("IN_PROGRESS"));
    }

    @Test
    void shouldSearchTransactionsWithMultipleFilters() throws Exception {
        TransportTransactionResponseDto transaction = new TransportTransactionResponseDto();
        transaction.setId(1L);
        transaction.setStatus("IN_PROGRESS");

        when(transactionService.search(null, 1L, null, "IN_PROGRESS")).thenReturn(List.of(transaction));

        mockMvc.perform(get("/transactions")
                        .param("truckId", "1")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void shouldCompleteTransaction() throws Exception {
        TransportTransactionResponseDto response = new TransportTransactionResponseDto();
        response.setId(1L);
        response.setStatus("COMPLETED");

        when(transactionService.complete(1L)).thenReturn(response);

        mockMvc.perform(patch("/transactions/1/complete")
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    void shouldCancelTransaction() throws Exception {
        TransportTransactionResponseDto response = new TransportTransactionResponseDto();
        response.setId(1L);
        response.setStatus("CANCELLED");

        when(transactionService.cancel(1L)).thenReturn(response);

        mockMvc.perform(patch("/transactions/1/cancel")
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    void shouldDeleteTransaction() throws Exception {
        mockMvc.perform(delete("/transactions/1")
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Transaction deleted successfully"));
    }
}
