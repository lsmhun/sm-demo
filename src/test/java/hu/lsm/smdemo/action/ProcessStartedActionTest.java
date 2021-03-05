package hu.lsm.smdemo.action;

import hu.lsm.smdemo.service.BargainClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import static hu.lsm.smdemo.model.Constants.BARGAIN_START;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcessStartedActionTest {

    private BargainClock bargainClock = mock(BargainClock.class);
    private ProcessStartedAction processStartedAction = new ProcessStartedAction(bargainClock);

    @Test
    public void execute() {
        var clock = Clock.fixed(Instant.ofEpochMilli(123), ZoneId.of("UTC"));
        when(bargainClock.getCurrentTime()).thenReturn(clock);
        var stateContext = mock(StateContext.class);
        var extendedState = mock(ExtendedState.class);
        when(stateContext.getExtendedState()).thenReturn(extendedState);
        var map = new HashMap<Object, Object>();
        when(stateContext.getExtendedState().getVariables()).thenReturn(map);
        processStartedAction.execute(stateContext);
        assertEquals(clock.millis(), stateContext.getExtendedState().getVariables().get(BARGAIN_START));
    }
}