package br.com.challenge.weighing_management_service.controller;

import br.com.challenge.weighing_management_service.dto.GrainTypeRequestDto;
import br.com.challenge.weighing_management_service.dto.GrainTypeResponseDto;
import br.com.challenge.weighing_management_service.service.GrainTypeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GrainTypeController.class)
@org.springframework.test.context.ActiveProfiles("test")
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class GrainTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GrainTypeService grainTypeService;

    @Test
    void shouldCreateGrainType() throws Exception {
        GrainTypeRequestDto request = new GrainTypeRequestDto();
        request.setName("Soja");
        request.setDescription("Soja tipo grão");
        request.setPurchasePricePerTon(BigDecimal.valueOf(150.50));

        GrainTypeResponseDto response = new GrainTypeResponseDto();
        response.setId(1L);
        response.setName("Soja");
        response.setDescription("Soja tipo grão");
        response.setPurchasePricePerTon(BigDecimal.valueOf(150.50));

        when(grainTypeService.create(any(GrainTypeRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/grain-types")
                        
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Soja"))
                .andExpect(jsonPath("$.data.description").value("Soja tipo grão"));
    }

    @Test
    void shouldSearchAllGrainTypes() throws Exception {
        GrainTypeResponseDto grainType1 = new GrainTypeResponseDto();
        grainType1.setId(1L);
        grainType1.setName("Soja");

        GrainTypeResponseDto grainType2 = new GrainTypeResponseDto();
        grainType2.setId(2L);
        grainType2.setName("Milho");

        List<GrainTypeResponseDto> grainTypes = Arrays.asList(grainType1, grainType2);

        when(grainTypeService.search(null)).thenReturn(grainTypes);

        mockMvc.perform(get("/grain-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].name").value("Soja"))
                .andExpect(jsonPath("$.data[1].name").value("Milho"));
    }

    @Test
    void shouldSearchGrainTypeById() throws Exception {
        GrainTypeResponseDto grainType = new GrainTypeResponseDto();
        grainType.setId(1L);
        grainType.setName("Soja");

        when(grainTypeService.search(1L)).thenReturn(List.of(grainType));

        mockMvc.perform(get("/grain-types")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Soja"));
    }

    @Test
    void shouldUpdateGrainType() throws Exception {
        GrainTypeRequestDto request = new GrainTypeRequestDto();
        request.setName("Soja Premium");
        request.setDescription("Soja tipo grão premium");
        request.setPurchasePricePerTon(BigDecimal.valueOf(175.00));

        GrainTypeResponseDto response = new GrainTypeResponseDto();
        response.setId(1L);
        response.setName("Soja Premium");
        response.setDescription("Soja tipo grão premium");
        response.setPurchasePricePerTon(BigDecimal.valueOf(175.00));

        when(grainTypeService.update(eq(1L), any(GrainTypeRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/grain-types/1")
                        
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Soja Premium"))
                .andExpect(jsonPath("$.data.description").value("Soja tipo grão premium"));
    }

    @Test
    void shouldDeleteGrainType() throws Exception {
        mockMvc.perform(delete("/grain-types/1")
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Grain type deleted successfully"));
    }
}
