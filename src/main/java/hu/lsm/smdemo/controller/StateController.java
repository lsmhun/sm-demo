package hu.lsm.smdemo.controller;

import hu.lsm.smdemo.service.StateMachineManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("${server.servlet.context-path}/api/v1/status")
public class StateController {

    private final StateMachineManager stateMachineManager;

    @GetMapping("currentState")
    public String getCurrentState(){
        return stateMachineManager.getCurrentState().name();
    }
}
