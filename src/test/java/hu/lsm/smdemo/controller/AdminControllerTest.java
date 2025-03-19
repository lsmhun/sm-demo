package hu.lsm.smdemo.controller;

import hu.lsm.smdemo.dao.DealDao;
import hu.lsm.smdemo.entity.Deal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private DealDao dealDao;

    @BeforeEach
    void init() {
        var res = Collections.singletonList(
                Deal.builder()
                        .id(UUID.fromString("696b06b1-3989-478f-b47e-fdc9a5b2af69"))
                        .price(1.2)
                        .timestamp(123L)
                        .user("user")
                        .build()
        );
        when(dealDao.findAll()).thenReturn(res);
    }

    @Test
    void startBargainManual() throws Exception {
        mvc.perform(get("/api/v1/admin/manualStart"))
                .andExpect(status().isOk());
    }

    @Test
    void stopBargainManual() throws Exception {
        mvc.perform(get("/api/v1/admin/manualStop"))
                .andExpect(status().isOk());
    }

    @Test
    void allCompletedDeal() throws Exception {
        mvc.perform(get("/api/v1/admin/allCompletedDeal"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":\"696b06b1-3989-478f-b47e-fdc9a5b2af69\",\"timestamp\":123,\"user\":\"user\",\"price\":1.2}]"));
    }
}