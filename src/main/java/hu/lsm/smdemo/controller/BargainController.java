package hu.lsm.smdemo.controller;

import hu.lsm.smdemo.model.DealOffer;
import hu.lsm.smdemo.service.AuthenticationFacade;
import hu.lsm.smdemo.service.BargainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("${server.servlet.context-path}/api/v1/bargain")
public class BargainController {

    private final BargainService bargainService;
    private final AuthenticationFacade authenticationFacade;

    @PostMapping(value = "sendOffer", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void sendOffer(@RequestBody DealOffer dealOffer){
        var auth = authenticationFacade.getAuthentication();
        bargainService.collectOffer(auth.getName(), dealOffer.getPrice());
    }
}
