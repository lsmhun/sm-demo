package hu.lsm.smdemo.service;

import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class BargainClock {
    public Clock getCurrentTime() {
        return java.time.Clock.systemUTC();
    }
}
