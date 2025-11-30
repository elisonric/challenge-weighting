package br.com.challenge.weighing_management_service.controller;

import br.com.challenge.weighing_management_service.dto.ScaleRequestDto;
import br.com.challenge.weighing_management_service.dto.ScaleResponseDto;
import br.com.challenge.weighing_management_service.service.ScaleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ScaleController.class)
@org.springframework.test.context.ActiveProfiles("test")
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class ScaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScaleService scaleService;

    @MockBean
    private br.com.challenge.weighing_management_service.config.JwtAuthFilter jwtAuthFilter;

    @MockBean
    private br.com.challenge.weighing_management_service.utils.JwtUtils jwtUtils;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Test
    void shouldCreateScale() throws Exception {
        ScaleRequestDto request = new ScaleRequestDto();
        request.setCode("SCALE-001");
        request.setLocation("Entrance");
        request.setBranchId(1L);
        request.setActive(true);

        ScaleResponseDto response = new ScaleResponseDto();
        response.setId(1L);
        response.setCode("SCALE-001");
        response.setLocation("Entrance");
        response.setActive(true);

        when(scaleService.create(any(ScaleRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/scales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.code").value("SCALE-001"));
    }

    @Test
    void shouldSearchAllScales() throws Exception {
        ScaleResponseDto scale1 = new ScaleResponseDto();
        scale1.setId(1L);
        scale1.setCode("SCALE-001");

        ScaleResponseDto scale2 = new ScaleResponseDto();
        scale2.setId(2L);
        scale2.setCode("SCALE-002");

        List<ScaleResponseDto> scales = Arrays.asList(scale1, scale2);

        when(scaleService.search(null, null, null, null)).thenReturn(scales);

        mockMvc.perform(get("/scales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].code").value("SCALE-001"))
                .andExpect(jsonPath("$.data[1].code").value("SCALE-002"));
    }

    @Test
    void shouldSearchScaleById() throws Exception {
        ScaleResponseDto scale = new ScaleResponseDto();
        scale.setId(1L);
        scale.setCode("SCALE-001");

        when(scaleService.search(1L, null, null, null)).thenReturn(List.of(scale));

        mockMvc.perform(get("/scales")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void shouldSearchScaleByCode() throws Exception {
        ScaleResponseDto scale = new ScaleResponseDto();
        scale.setId(1L);
        scale.setCode("SCALE-001");

        when(scaleService.search(null, "SCALE-001", null, null)).thenReturn(List.of(scale));

        mockMvc.perform(get("/scales")
                        .param("code", "SCALE-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].code").value("SCALE-001"));
    }

    @Test
    void shouldSearchScalesByBranchId() throws Exception {
        ScaleResponseDto scale = new ScaleResponseDto();
        scale.setId(1L);
        scale.setCode("SCALE-001");

        when(scaleService.search(null, null, 1L, null)).thenReturn(List.of(scale));

        mockMvc.perform(get("/scales")
                        .param("branchId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void shouldSearchActiveScales() throws Exception {
        ScaleResponseDto scale = new ScaleResponseDto();
        scale.setId(1L);
        scale.setCode("SCALE-001");
        scale.setActive(true);

        when(scaleService.search(null, null, null, true)).thenReturn(List.of(scale));

        mockMvc.perform(get("/scales")
                        .param("activeOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].active").value(true));
    }

    @Test
    void shouldUpdateScale() throws Exception {
        ScaleRequestDto request = new ScaleRequestDto();
        request.setCode("SCALE-001");
        request.setLocation("Updated Location");
        request.setBranchId(1L);
        request.setActive(true);

        ScaleResponseDto response = new ScaleResponseDto();
        response.setId(1L);
        response.setCode("SCALE-001");
        response.setLocation("Updated Location");

        when(scaleService.update(eq(1L), any(ScaleRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/scales/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.location").value("Updated Location"));
    }

    @Test
    void shouldDeleteScale() throws Exception {
        mockMvc.perform(delete("/scales/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Scale deleted successfully"));
    }
}
