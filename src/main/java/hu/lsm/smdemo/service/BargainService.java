package hu.lsm.smdemo.service;

import hu.lsm.smdemo.exception.MarketClosedException;
import hu.lsm.smdemo.model.AppState;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class BargainService {

    private final StateMachineManager stateMachineManager;
    private final BargainClock bargainClock;

    @Getter
    private Map<Pair<String, Long>, Double> offers = new ConcurrentHashMap<>();

    public void collectOffer(String user, Double price) {
        if (isBargainActive()) {
            Long currentTimeInMillis = bargainClock.getCurrentTime().millis();
            offers.put(Pair.of(user, currentTimeInMillis), price);
        } else {
            throw new MarketClosedException("Market is closed.");
        }
    }

    public Optional<Triple<String, Long, Double>> getCurrentLeader() {
        return offers.entrySet()
                .stream().min((e1, e2) ->
                        e2.getValue().compareTo(e1.getValue()) * 1000 +
                                e1.getKey().getRight().compareTo(e2.getKey().getRight()))
                .map(e -> Triple.of(
                        e.getKey().getLeft(),
                        e.getKey().getRight(),
                        e.getValue()
                ));
    }

    public void cleanup() {
        if (isBargainActive()) {
            log.error("During bargain cleanup is prohibited!");
        } else {
            offers.clear();
        }
    }

    private boolean isBargainActive() {
        return stateMachineManager.getCurrentState() != AppState.WAITING;
    }



}
