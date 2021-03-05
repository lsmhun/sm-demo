package hu.lsm.smdemo.action;

import hu.lsm.smdemo.model.AppEvent;
import hu.lsm.smdemo.model.AppState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import static hu.lsm.smdemo.model.Constants.BARGAIN_START;

@Component
@Slf4j
public class ProcessFailedAction implements Action<AppState, AppEvent> {
    @Override
    public void execute(StateContext<AppState, AppEvent> stateContext) {
        var variables = stateContext.getExtendedState().getVariables();
        var timestamp = variables.get(BARGAIN_START);
        log.info("Bargain startTime={} Process FAILED: No offer arrived", timestamp);
    }
}
