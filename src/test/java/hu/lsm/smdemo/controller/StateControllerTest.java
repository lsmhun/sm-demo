package hu.lsm.smdemo.controller;

import hu.lsm.smdemo.model.AppState;
import hu.lsm.smdemo.service.StateMachineManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StateControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private StateMachineManager stateMachineManager;

    @BeforeEach
    void init() {
        when(stateMachineManager.getCurrentState()).thenReturn(AppState.WAITING);
    }

    @Test
    public void getCurrentState() throws Exception {
        mvc.perform(get("/api/v1/status/currentState"))
                .andExpect(status().isOk())
                .andExpect(content().string(AppState.WAITING.name()));
    }
}