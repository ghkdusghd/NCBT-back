package kr.kh.backend.service;

import kr.kh.backend.dto.PracticeComplaintsDTO;
import kr.kh.backend.mapper.AdminMapper;
import kr.kh.backend.service.security.EmailVerificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;
    private final EmailVerificationService emailVerificationService;

    public List<PracticeComplaintsDTO> getAllComplaints() {
        List<PracticeComplaintsDTO> complaintsList = adminMapper.selectAllComplaints();
        return complaintsList;
    }

    public int solvedComplaints(PracticeComplaintsDTO practiceComplaintsDTO) {
        // 사용자에게 메일 발송
        emailVerificationService.sendSolvedMsgToUser(practiceComplaintsDTO.getUserId());

        // DB항목 update
        return adminMapper.updateSolvedComplaints(practiceComplaintsDTO.getId());
    }

}
