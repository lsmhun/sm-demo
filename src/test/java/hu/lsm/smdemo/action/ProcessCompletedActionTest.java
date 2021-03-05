package hu.lsm.smdemo.action;

import hu.lsm.smdemo.service.BargainService;
import org.junit.jupiter.api.Test;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateContext;

import java.util.Map;

import static hu.lsm.smdemo.model.Constants.BARGAIN_START;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcessCompletedActionTest {

    private ProcessCompletedAction processCompletedAction = new ProcessCompletedAction();

    @Test
    public void execute() {
        var stateContext = mock(StateContext.class);
        var extendedState = mock(ExtendedState.class);
        when(stateContext.getExtendedState()).thenReturn(extendedState);
        when(stateContext.getExtendedState().getVariables()).thenReturn(mock(Map.class));
        processCompletedAction.execute(stateContext);
        verify(stateContext.getExtendedState().getVariables()).get(BARGAIN_START);
    }
}