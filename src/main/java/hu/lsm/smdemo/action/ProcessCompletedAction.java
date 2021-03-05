package hu.lsm.smdemo.action;

import hu.lsm.smdemo.model.AppEvent;
import hu.lsm.smdemo.model.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import static hu.lsm.smdemo.model.Constants.BARGAIN_START;

@RequiredArgsConstructor
@Component
@Slf4j
public class ProcessCompletedAction implements Action<AppState, AppEvent> {


    @Override
    public void execute(StateContext<AppState, AppEvent> stateContext) {
        var variables = stateContext.getExtendedState().getVariables();
        var timestamp = variables.get(BARGAIN_START);
        log.info("Bargain startTime={} SUCCESS", timestamp);
    }
}
