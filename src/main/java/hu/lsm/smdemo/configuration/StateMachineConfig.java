package hu.lsm.smdemo.configuration;

import hu.lsm.smdemo.action.ProcessCompletedAction;
import hu.lsm.smdemo.action.ProcessFailedAction;
import hu.lsm.smdemo.model.AppEvent;
import hu.lsm.smdemo.model.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@RequiredArgsConstructor
@EnableStateMachineFactory
@Configuration
@Slf4j
public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<AppState, AppEvent> {

    private final ProcessCompletedAction processCompletedAction;
    private final ProcessFailedAction processFailedAction;

    @Override
    public void configure(StateMachineConfigurationConfigurer<AppState, AppEvent> config) throws Exception {
        StateMachineListenerAdapter<AppState, AppEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<AppState, AppEvent> from, State<AppState, AppEvent> to) {
                super.stateChanged(from, to);
                if (from == null || to == null) return;
                log.info("State changed: {} --> {} ", from.getId(), to.getId());
            }
        };
        config.withConfiguration().listener(adapter);
    }

    @Override
    public void configure(StateMachineStateConfigurer<AppState, AppEvent> states) throws Exception {
        states.withStates().initial(AppState.INIT)
                .states(EnumSet.allOf(AppState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<AppState, AppEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(AppState.INIT).target(AppState.WAITING)
                .and().withExternal()
                .source(AppState.WAITING).target(AppState.RUNNING).event(AppEvent.START)
                .and().withExternal()
                .source(AppState.RUNNING).target(AppState.COMPLETED).event(AppEvent.COMPLETE)
                .and().withExternal()
                .source(AppState.RUNNING).target(AppState.FAILED).event(AppEvent.FAIL)
                .and().withExternal()
                .source(AppState.COMPLETED).target(AppState.WAITING)
                .action(processCompletedAction)
                .and().withExternal()
                .source(AppState.FAILED).target(AppState.WAITING)
                .action(processFailedAction);
    }
}
