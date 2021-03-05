package hu.lsm.smdemo.service;

import hu.lsm.smdemo.dao.DealDao;
import hu.lsm.smdemo.entity.Deal;
import hu.lsm.smdemo.model.AppEvent;
import hu.lsm.smdemo.model.AppState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulingService {

    private final StateMachineManager stateMachineManager;
    private final BargainService bargainService;
    private final DealDao dealDao;

    @Scheduled(cron = "${bargain.start.cron:0 0/5 * * * ?}", zone = "${bargain.zone:UTC}")
    public void startBargain() {
        var currentState = stateMachineManager.getCurrentState();
        if (currentState == AppState.WAITING) {
            stateMachineManager.sendEvent(AppEvent.START);
        } else {
            log.error("Application is not ready to start a bargain. currentState={}", currentState);
        }
    }

    @Scheduled(cron = "${bargain.stop.cron:0 1/5 * * * ?}", zone = "${bargain.zone:UTC}")
    public void stopBargain() {
        var currentState = stateMachineManager.getCurrentState();
        if (currentState == AppState.RUNNING) {
            try {
                var bargainResult = bargainService.getCurrentLeader();
                if (bargainResult.isPresent()) {
                    stateMachineManager.sendEvent(AppEvent.COMPLETE);
                    var result = bargainResult.get();
                    dealDao.save(Deal.builder()
                            .id(UUID.randomUUID())
                            .user(result.getLeft())
                            .timestamp(result.getMiddle())
                            .price(result.getRight())
                            .build());
                } else {
                    stateMachineManager.sendEvent(AppEvent.FAIL);
                }
            } finally {
                bargainService.cleanup();
            }
        } else {
            log.error("Application is not in RUNNING state. currentState={}", currentState);
        }
    }
}
