package br.com.challenge.weighing_management_service.controller;

import br.com.challenge.weighing_management_service.dto.TruckRequestDto;
import br.com.challenge.weighing_management_service.dto.TruckResponseDto;
import br.com.challenge.weighing_management_service.service.TruckService;
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

@WebMvcTest(controllers = TruckController.class)
@org.springframework.test.context.ActiveProfiles("test")
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class TruckControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TruckService truckService;

    @Test
    void shouldCreateTruck() throws Exception {
        TruckRequestDto request = new TruckRequestDto();
        request.setPlate("ABC-1234");
        request.setModel("Scania R450");
        request.setTareWeight(BigDecimal.valueOf(8500.00));
        request.setBranchId(1L);

        TruckResponseDto response = new TruckResponseDto();
        response.setId(1L);
        response.setPlate("ABC-1234");
        response.setModel("Scania R450");
        response.setTareWeight(BigDecimal.valueOf(8500.00));

        when(truckService.create(any(TruckRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/trucks")
                        
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.plate").value("ABC-1234"))
                .andExpect(jsonPath("$.data.model").value("Scania R450"));
    }

    @Test
    void shouldSearchAllTrucks() throws Exception {
        TruckResponseDto truck1 = new TruckResponseDto();
        truck1.setId(1L);
        truck1.setPlate("ABC-1234");

        TruckResponseDto truck2 = new TruckResponseDto();
        truck2.setId(2L);
        truck2.setPlate("XYZ-5678");

        List<TruckResponseDto> trucks = Arrays.asList(truck1, truck2);

        when(truckService.search(null, null)).thenReturn(trucks);

        mockMvc.perform(get("/trucks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].plate").value("ABC-1234"))
                .andExpect(jsonPath("$.data[1].plate").value("XYZ-5678"));
    }

    @Test
    void shouldSearchTruckById() throws Exception {
        TruckResponseDto truck = new TruckResponseDto();
        truck.setId(1L);
        truck.setPlate("ABC-1234");

        when(truckService.search(1L, null)).thenReturn(List.of(truck));

        mockMvc.perform(get("/trucks")
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void shouldSearchTruckByPlate() throws Exception {
        TruckResponseDto truck = new TruckResponseDto();
        truck.setId(1L);
        truck.setPlate("ABC-1234");

        when(truckService.search(null, "ABC-1234")).thenReturn(List.of(truck));

        mockMvc.perform(get("/trucks")
                        .param("plate", "ABC-1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].plate").value("ABC-1234"));
    }

    @Test
    void shouldUpdateTruck() throws Exception {
        TruckRequestDto request = new TruckRequestDto();
        request.setPlate("ABC-1234");
        request.setModel("Scania R500");
        request.setTareWeight(BigDecimal.valueOf(9000.00));
        request.setBranchId(1L);

        TruckResponseDto response = new TruckResponseDto();
        response.setId(1L);
        response.setPlate("ABC-1234");
        response.setModel("Scania R500");
        response.setTareWeight(BigDecimal.valueOf(9000.00));

        when(truckService.update(eq(1L), any(TruckRequestDto.class))).thenReturn(response);

        mockMvc.perform(put("/trucks/1")
                        
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.model").value("Scania R500"));
    }

    @Test
    void shouldDeleteTruck() throws Exception {
        mockMvc.perform(delete("/trucks/1")
                        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Truck deleted successfully"));
    }
}
