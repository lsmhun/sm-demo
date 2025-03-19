package hu.lsm.smdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.lsm.smdemo.model.DealOffer;
import hu.lsm.smdemo.service.AuthenticationFacade;
import hu.lsm.smdemo.service.BargainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BargainControllerTest {

    @MockitoBean
    private BargainService bargainService;

    @MockitoBean
    private AuthenticationFacade authenticationFacade;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void init() {
        var auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user");
        when(authenticationFacade.getAuthentication()).thenReturn(auth);
    }

    @Test
    public void sendOffer() throws Exception {
        var offer = DealOffer.builder().price(1.2).build();
        mvc.perform(post("/api/v1/bargain/sendOffer")
                .content(asJsonString(offer))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}