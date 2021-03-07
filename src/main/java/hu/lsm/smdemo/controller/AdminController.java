package hu.lsm.smdemo.controller;

import hu.lsm.smdemo.dao.DealDao;
import hu.lsm.smdemo.entity.Deal;
import hu.lsm.smdemo.service.SchedulingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("${server.servlet.context-path}/api/v1/admin")
public class AdminController {

    private final SchedulingService schedulingService;
    private final DealDao dealDao;

    @GetMapping("manualStart")
    public void startBargainManual() {
        schedulingService.startBargain();
    }

    @GetMapping("manualStop")
    public void stopBargainManual() {
        schedulingService.stopBargain();
    }

    @GetMapping("allCompletedDeal")
    public List<Deal> allCompletedDeal() {
        return dealDao.findAll()
                .stream()
                .sorted(Comparator.comparingLong(Deal::getTimestamp))
                .collect(Collectors.toList());
    }
}
