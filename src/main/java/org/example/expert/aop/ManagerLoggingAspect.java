package org.example.expert.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.log.service.LogService;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ManagerLoggingAspect {

    private final LogService logService;

    // ManagerService의 saveManager 메서드가 실행되기 직전(@Before)에 동작
    @Before("execution(* org.example.expert.domain.manager.service.ManagerService.saveManager(..))")
    public void logManagerRegistration(JoinPoint joinPoint) {
        
        // 가로챈 메서드의 파라미터 추출
        Object[] args = joinPoint.getArgs();
        AuthUser authUser = (AuthUser) args[0];
        long todoId = (long) args[1];
        ManagerSaveRequest request = (ManagerSaveRequest) args[2];

        // 로그 메시지 생성 및 독립 트랜잭션으로 저장
        String logMessage = String.format("매니저 등록 요청 - 일정 ID: %d, 요청 유저 ID: %d, 등록할 매니저 ID: %d",
                todoId, authUser.getId(), request.getManagerUserId());
        
        logService.saveLog(logMessage);
    }
}
