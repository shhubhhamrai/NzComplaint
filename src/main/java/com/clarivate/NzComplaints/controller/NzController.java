package com.clarivate.NzComplaints.controller;

import com.clarivate.NzComplaints.models.Binder;
import com.clarivate.NzComplaints.service.NzipotmComplaintService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/nz")
public class NzController {

    private final NzipotmComplaintService nzipotmComplaintService;

    public NzController(NzipotmComplaintService nzipotmComplaintService) {
        this.nzipotmComplaintService = nzipotmComplaintService;
    }

    @GetMapping("/run")
    public ResponseEntity<Binder> runRobot() {
        Binder binder = nzipotmComplaintService.runRobot();
        return ResponseEntity.ok(binder);
    }
}
