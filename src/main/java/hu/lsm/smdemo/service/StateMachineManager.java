package hu.lsm.smdemo.service;

import hu.lsm.smdemo.model.AppEvent;
import hu.lsm.smdemo.model.AppState;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class StateMachineManager {

    private final StateMachineFactory<AppState, AppEvent> stateMachineFactory;
    private StateMachine<AppState, AppEvent> stateMachine;

    @PostConstruct
    private void init() {
        stateMachine = stateMachineFactory.getStateMachine();
        stateMachine.start();
    }

    public AppState getCurrentState() {
        return stateMachine.getState().getId();
    }

    public void sendEvent(AppEvent appEvent) {
        stateMachine.sendEvent(appEvent);
    }

}
