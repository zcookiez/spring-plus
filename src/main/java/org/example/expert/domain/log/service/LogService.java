package org.example.expert.domain.log.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogService {

    private final LogRepository logRepository;

    /**
     * 로그 저장은 부모 트랜잭션(예: 매니저 등록)의 성공/실패 여부와 무관하게 
     * 무조건 DB에 반영되어야 하므로 REQUIRES_NEW 옵션 사용
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(String message) {
        Log log = new Log(message);
        logRepository.save(log);
    }
}
