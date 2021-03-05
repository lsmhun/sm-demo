package hu.lsm.smdemo.config;

import hu.lsm.smdemo.controller.StateController;
import hu.lsm.smdemo.model.AppEvent;
import hu.lsm.smdemo.model.AppState;
import hu.lsm.smdemo.service.BargainService;
import hu.lsm.smdemo.service.SchedulingService;
import hu.lsm.smdemo.service.StateMachineManager;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@TestConfiguration
public class StateMachineTestConfig {

//    @MockBean
//    private SchedulingService schedulingService;
//    @MockBean
//    private BargainService bargainService;
//    @MockBean
//    private StateController stateController;

    @Autowired
    private StateMachineFactory<AppState, AppEvent> stateMachineFactory;
    @Autowired
    private TestStateMachineListener stateMachineListener;
    @Autowired
    private StateMachine<AppState, AppEvent> stateMachine;

    @Bean
    public TestStateMachineListener stateMachineListener() {
        return new TestStateMachineListener();
    }

    @Bean
    public StateMachine<AppState, AppEvent> stateMachine() {
        var sm = stateMachineFactory.getStateMachine();
        sm.addStateListener(stateMachineListener);
        return sm;
    }

    @Bean
    @Primary
    public StateMachineFactory<AppState, AppEvent> mockSMFactory() {
        var mf = mock(StateMachineFactory.class);
        when(mf.getStateMachine()).thenReturn(stateMachine);
        return mf;
    }

    @Bean
    @Primary
    public StateMachineManager stateMachineManager() {
        return spy(new StateMachineManager(mockSMFactory()));
    }

    public static class TestStateMachineListener extends StateMachineListenerAdapter<AppState, AppEvent> {

        @Getter
        private List<State<AppState, AppEvent>> stateList = new ArrayList<>();

        @Override
        public void stateEntered(State<AppState, AppEvent> state) {
            stateList.add(state);
        }

        @Override
        public void stateExited(State<AppState, AppEvent> state) {
            stateList.add(state);
        }

        public void cleanup() {
            stateList.clear();
        }
    }
}
