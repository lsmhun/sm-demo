package hu.lsm.smdemo.service;

import hu.lsm.smdemo.action.ProcessCompletedAction;
import hu.lsm.smdemo.action.ProcessFailedAction;
import hu.lsm.smdemo.config.StateMachineTestConfig;
import hu.lsm.smdemo.model.AppEvent;
import hu.lsm.smdemo.model.AppState;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.statemachine.state.State;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Import(StateMachineTestConfig.class)
class StateFlowTest {

    @MockBean
    private ProcessFailedAction processFailedAction;
    @MockBean
    private ProcessCompletedAction processCompletedAction;

    @Autowired
    private StateMachineManager stateMachineManager;

    @Autowired
    private StateMachineTestConfig.TestStateMachineListener stateMachineListener;

    static {
        Awaitility.setDefaultPollInterval(100, TimeUnit.MILLISECONDS);
        Awaitility.setDefaultTimeout(Duration.ofSeconds(10L));
    }

    @BeforeEach
    public void init() {
        stateMachineListener.cleanup();
    }

    @Test
    public void testCompletedFlow() {
        stateMachineManager.sendEvent(AppEvent.START);
        await().until(stateMachineManager::getCurrentState, equalTo(AppState.RUNNING));
        stateMachineManager.sendEvent(AppEvent.COMPLETE);
        await().until(stateMachineManager::getCurrentState, equalTo(AppState.WAITING));
        var expectedList = Arrays.asList(
                AppState.WAITING, AppState.RUNNING,
                AppState.RUNNING, AppState.COMPLETED,
                AppState.COMPLETED, AppState.WAITING);
        assertArrayEquals(expectedList.toArray(), stateMachineListener.getStateList().stream().map(State::getId).toArray());
        verify(processCompletedAction).execute(any());
    }

    @Test
    public void testFailedFlow() {
        stateMachineManager.sendEvent(AppEvent.START);
        await().until(stateMachineManager::getCurrentState, equalTo(AppState.RUNNING));
        stateMachineManager.sendEvent(AppEvent.FAIL);
        await().until(stateMachineManager::getCurrentState, equalTo(AppState.WAITING));
        var expectedList = Arrays.asList(
                AppState.WAITING, AppState.RUNNING,
                AppState.RUNNING, AppState.FAILED,
                AppState.FAILED, AppState.WAITING);
        assertArrayEquals(expectedList.toArray(), stateMachineListener.getStateList().stream().map(State::getId).toArray());
        verify(processFailedAction).execute(any());
    }
}