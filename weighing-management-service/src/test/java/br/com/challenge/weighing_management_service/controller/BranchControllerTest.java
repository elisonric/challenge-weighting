package br.com.challenge.weighing_management_service.controller;

import br.com.challenge.weighing_management_service.dto.BranchRequestDto;
import br.com.challenge.weighing_management_service.dto.BranchResponseDto;
import br.com.challenge.weighing_management_service.service.BranchService;
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

@WebMvcTest(controllers = BranchController.class)
@org.springframework.test.context.ActiveProfiles("test")
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class BranchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BranchService branchService;

    @Test
    void shouldCreateBranch() throws Exception {
        BranchRequestDto request = new BranchRequestDto();
        request.setName("Filial Centro");
        request.setCity("São Paulo");
        request.setState("SP");
        request.setAddress("Rua Principal, 123");

        BranchResponseDto response = new BranchResponseDto();
        response.setId(1L);
        response.setName("Filial Centro");
        response.setCity("São Paulo");
        response.setState("SP");

        when(branchService.create(any(BranchRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/branches")
                        
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Filial Centro"))
                .andExpect(jsonPath("$.data.city").value("São Paulo"));
    }

    @Test
    void shouldSearchAllBranches() throws Exception {
        BranchResponseDto branch1 = new BranchResponseDto();
        branch1.setId(1L);
        branch1.setName("Filial Centro");

        BranchResponseDto branch2 = new BranchResponseDto();
        branch2.setId(2L);
        branch2.setName("Filial Norte");

        List<BranchResponseDto> branches = Arrays.asList(branch1, branch2);

        when(branchService.search(null)).thenReturn(branches);

        mockMvc.perform(get("/branches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("Filial Centro"))
                .andExpect(jsonPath("$.data[1].name").value("Filial Norte"));
    }

    @Test
    void shouldSearchBranchById() throws Exception {
        BranchResponseDto branch = new BranchResponseDto();
        branch.setId(1L);
        branch.setName("Filial Centro");

        when(branchService.search(1L)).thenReturn(List.of(branch));

        mockMvc.perform(get("/branches")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Filial Centro"));
    }

    @Test
    void shouldUpdateBranch() throws Exception {
        BranchRequestDto request = new BranchRequestDto();
        request.setName("Filial Centro Atualizada");
        request.setCity("São Paulo");
        request.setState("SP");
        request.setAddress("Rua Principal, 456");

        BranchResponseDto response = new BranchResponseDto();
        response.setId(1L);
        response.setName("Filial Centro Atualizada");
        response.setCity("São Paulo");

        when(branchService.update(eq(1L), any(BranchRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/branches/1")
                        
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Filial Centro Atualizada"));
    }

    @Test
    void shouldDeleteBranch() throws Exception {
        mockMvc.perform(delete("/branches/1")
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Branch deleted successfully"));
    }
}
