package kr.kh.backend.controller;

import kr.kh.backend.dto.PracticeComplaintsDTO;
import kr.kh.backend.service.AdminService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/admin/complaints")
    public ResponseEntity<?> getAllComplaints() {
        try {
            List<PracticeComplaintsDTO> complaintsList = adminService.getAllComplaints();
            return ResponseEntity.ok().body(complaintsList);
        } catch (Exception e) {
            log.info(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/admin/solvedComplaints")
    public ResponseEntity<?> solvedComplaints(@RequestBody PracticeComplaintsDTO practiceComplaintsDTO) {
        log.info("문제 오류 해결 ! 신고한 사용자 = {}", practiceComplaintsDTO.getUserId());
        int result = adminService.solvedComplaints(practiceComplaintsDTO);

        if(result == 1) {
            return ResponseEntity.status(200).build();
        } else {
            return ResponseEntity.status(500).build();
        }
    }

}
