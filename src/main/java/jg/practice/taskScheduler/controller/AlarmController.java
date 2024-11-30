package jg.practice.taskScheduler.controller;

import jg.practice.taskScheduler.dto.request.AlarmCreateReq;
import jg.practice.taskScheduler.dto.request.AlarmUpdateReq;
import jg.practice.taskScheduler.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @PostMapping("api/alarm")
    public ResponseEntity<Long> create(
            @Validated @RequestBody AlarmCreateReq req
    ) {
        return ResponseEntity.ok(alarmService.create(req));
    }

    @PutMapping("api/alarm")
    public ResponseEntity<Long> update(
            @Validated @RequestBody AlarmUpdateReq req
    ) {
        return ResponseEntity.ok(alarmService.update(req));
    }
}
