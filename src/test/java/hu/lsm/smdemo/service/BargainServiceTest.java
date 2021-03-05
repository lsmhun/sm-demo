package hu.lsm.smdemo.service;

import hu.lsm.smdemo.exception.MarketClosedException;
import hu.lsm.smdemo.model.AppState;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BargainServiceTest {

    private StateMachineManager stateMachineManager = mock(StateMachineManager.class);
    private BargainClock bargainClock = mock(BargainClock.class);

    private BargainService bargainService = new BargainService(stateMachineManager, bargainClock);

    @BeforeEach
    private void init(){
        when(stateMachineManager.getCurrentState()).thenReturn(AppState.RUNNING);
        when(bargainClock.getCurrentTime()).thenReturn(Clock.fixed(Instant.ofEpochMilli(123), ZoneId.of("UTC")));
    }

    @Test
    public void testFlow(){
        bargainService.collectOffer("user1", 1.2);
        when(bargainClock.getCurrentTime()).thenReturn(Clock.fixed(Instant.ofEpochMilli(123l), ZoneId.of("UTC")));
        bargainService.collectOffer("user1", 1.3);
        var expected = Triple.of("user1", 123l, 1.3);
        var result = bargainService.getCurrentLeader().get();
        assertEquals(expected, result);
    }

    @Test
    public void testSamePrice(){
        when(bargainClock.getCurrentTime()).thenReturn(Clock.fixed(Instant.ofEpochMilli(1), ZoneId.of("UTC")));
        bargainService.collectOffer("user0", 1.1);
        when(bargainClock.getCurrentTime()).thenReturn(Clock.fixed(Instant.ofEpochMilli(2), ZoneId.of("UTC")));
        bargainService.collectOffer("user1", 1.2);
        when(bargainClock.getCurrentTime()).thenReturn(Clock.fixed(Instant.ofEpochMilli(3), ZoneId.of("UTC")));
        bargainService.collectOffer("user2", 1.2);
        var expected = Triple.of("user1", 2l, 1.2);
        var result = bargainService.getCurrentLeader().get();
        assertEquals(expected, result);
    }

    @Test
    public void testMarketClosed(){
        when(stateMachineManager.getCurrentState()).thenReturn(AppState.WAITING);
        assertThrows(MarketClosedException.class, () -> {
            bargainService.collectOffer("user1", 1.2);
        });
    }

    @Test
    public void testResults(){
        bargainService.collectOffer("user1", 1.2);
        when(bargainClock.getCurrentTime()).thenReturn(Clock.fixed(Instant.ofEpochMilli(212), ZoneId.of("UTC")));
        bargainService.collectOffer("user2", 1.3);
        var offersInString = bargainService.getOffers().entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .map(e -> String.join("-", e.getKey().getLeft(), e.getKey().getRight().toString(), e.getValue().toString()))
                .collect(Collectors.joining(", ", "{", "}"));
        assertEquals("{user1-123-1.2, user2-212-1.3}", offersInString);
    }
}