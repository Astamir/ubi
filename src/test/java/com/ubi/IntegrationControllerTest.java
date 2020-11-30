package com.ubi;

import static com.ubi.api.model.ChargingPointState.StatusEnum.AVAILABLE;
import static com.ubi.api.model.ChargingPointState.StatusEnum.OCCUPIED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubi.api.model.ChargingPointState;
import com.ubi.controller.UbiCarParkController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
@WebMvcTest(controllers = UbiCarParkController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class IntegrationControllerTest {
  @Autowired private MockMvc mockMvc;
  private ObjectMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Test
  public void testFullFlow() throws Exception {
    plug(0);
    plug(1);
    plug(2);
    plug(3);
    plug(4);
    com.ubi.api.model.ChargingPointStateReport list = list();
    assertThat(list.getTotal()).isEqualTo(10);
    assertThat(list.getChargingPoints())
        .hasSize(10)
        .extracting(ChargingPointState::getConsumption)
        .containsSequence(20, 20, 20, 20, 20, 0, 0, 0, 0, 0);
    assertThat(list.getChargingPoints())
        .hasSize(10)
        .extracting(ChargingPointState::getStatus)
        .containsSequence(
            OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, AVAILABLE, AVAILABLE, AVAILABLE,
            AVAILABLE, AVAILABLE);
    plug(5);
    plug(6);
    list = list();
    assertThat(list.getChargingPoints())
        .hasSize(10)
        .extracting(ChargingPointState::getConsumption)
        .containsSequence(10, 10, 10, 10, 20, 20, 20, 0, 0, 0);
    assertThat(list.getChargingPoints())
        .hasSize(10)
        .extracting(ChargingPointState::getStatus)
        .containsSequence(
            OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, AVAILABLE,
            AVAILABLE, AVAILABLE);
    plug(7);
    plug(8);
    list = list();
    assertThat(list.getChargingPoints())
        .hasSize(10)
        .extracting(ChargingPointState::getConsumption)
        .containsSequence(10, 10, 10, 10, 10, 10, 10, 10, 20, 0);
    assertThat(list.getChargingPoints())
        .hasSize(10)
        .extracting(ChargingPointState::getStatus)
        .containsSequence(
            OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED,
            OCCUPIED, AVAILABLE);
    plug(9);
    list = list();
    assertThat(list.getChargingPoints())
        .hasSize(10)
        .extracting(ChargingPointState::getConsumption)
        .containsSequence(10, 10, 10, 10, 10, 10, 10, 10, 10, 10);
    assertThat(list.getChargingPoints())
        .hasSize(10)
        .extracting(ChargingPointState::getStatus)
        .containsSequence(
            OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED,
            OCCUPIED, OCCUPIED);
    unplug(3);
    list = list();
    assertThat(list.getChargingPoints())
        .hasSize(10)
        .extracting(ChargingPointState::getConsumption)
        .containsSequence(10, 10, 10, 0, 10, 10, 10, 10, 10, 20);
    assertThat(list.getChargingPoints())
        .hasSize(10)
        .extracting(ChargingPointState::getStatus)
        .containsSequence(
            OCCUPIED, OCCUPIED, OCCUPIED, AVAILABLE, OCCUPIED, OCCUPIED, OCCUPIED, OCCUPIED,
            OCCUPIED, OCCUPIED);
    unplug(5);
    list = list();
    assertThat(list.getChargingPoints())
        .hasSize(10)
        .extracting(ChargingPointState::getConsumption)
        .containsSequence(10, 10, 10, 0, 10, 0, 10, 10, 20, 20);
    assertThat(list.getChargingPoints())
        .hasSize(10)
        .extracting(ChargingPointState::getStatus)
        .containsSequence(
            OCCUPIED, OCCUPIED, OCCUPIED, AVAILABLE, OCCUPIED, AVAILABLE, OCCUPIED, OCCUPIED,
            OCCUPIED, OCCUPIED);
  }

  @Test
  public void testErrors() throws Exception {
    mockMvc.perform(get("/charging-points/" + 11)).andExpect(status().is(404));

    mockMvc.perform(put("/charging-points/" + 11)).andExpect(status().is(404));

    mockMvc.perform(put("/charging-points/" + 1)).andExpect(status().isOk());

    mockMvc.perform(put("/charging-points/" + 1)).andExpect(status().is(403));

    mockMvc.perform(delete("/charging-points/" + 0)).andExpect(status().is(403));
  }

  private com.ubi.api.model.ChargingPointState getPoints(int index) throws Exception {
    final String contentAsString =
        mockMvc
            .perform(get("/charging-points/" + index))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return mapper.readValue(contentAsString, com.ubi.api.model.ChargingPointState.class);
  }

  private com.ubi.api.model.ChargingPointState plug(int index) throws Exception {
    final String contentAsString =
        mockMvc
            .perform(put("/charging-points/" + index))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return mapper.readValue(contentAsString, com.ubi.api.model.ChargingPointState.class);
  }

  private com.ubi.api.model.ChargingPointState unplug(int index) throws Exception {
    final String contentAsString =
        mockMvc
            .perform(delete("/charging-points/" + index))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return mapper.readValue(contentAsString, com.ubi.api.model.ChargingPointState.class);
  }

  private com.ubi.api.model.ChargingPointStateReport list() throws Exception {
    final String contentAsString =
        mockMvc
            .perform(get("/charging-points"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    return mapper.readValue(contentAsString, com.ubi.api.model.ChargingPointStateReport.class);
  }

  @TestConfiguration
  @ComponentScan(basePackages = "com.ubi")
  static class ContextConfiguration {}
}
