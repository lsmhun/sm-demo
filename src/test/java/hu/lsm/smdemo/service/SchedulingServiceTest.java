package hu.lsm.smdemo.service;

import hu.lsm.smdemo.dao.DealDao;
import hu.lsm.smdemo.model.AppEvent;
import hu.lsm.smdemo.model.AppState;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

class SchedulingServiceTest {

    private StateMachineManager stateMachineManager = mock(StateMachineManager.class);
    private BargainService bargainService = mock(BargainService.class);
    private DealDao dealDao = mock(DealDao.class);

    private SchedulingService schedulingService = new SchedulingService(stateMachineManager, bargainService, dealDao);

    @BeforeEach
    private void setUp() {
        when(stateMachineManager.getCurrentState()).thenReturn(AppState.WAITING);
    }

    @Test
    public void startBargain() {
        schedulingService.startBargain();
        verify(stateMachineManager).sendEvent(AppEvent.START);
    }

    @Test
    public void startBargainNotInGoodState() {
        when(stateMachineManager.getCurrentState()).thenReturn(AppState.RUNNING);
        schedulingService.startBargain();
        verify(stateMachineManager, times(0)).sendEvent(AppEvent.START);
    }

    @Test
    public void stopBargain() {
        when(stateMachineManager.getCurrentState()).thenReturn(AppState.RUNNING);
        when(bargainService.getCurrentLeader()).thenReturn(Optional.of(Triple.of("user", 123L, 123.45)));

        schedulingService.stopBargain();
        verify(dealDao).save(any());
        verify(bargainService).cleanup();
    }

    @Test
    public void stopBargainNotInGoodState() {
        schedulingService.stopBargain();
        verify(dealDao, times(0)).save(any());
    }
}