package org.example.expert.domain.todo.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class TodoSearchResponse {

    private final String title;
    private final Long managerCount;
    private final Long commentCount;

    // Q-Class(QTodoSearchResponse)를 생성하여 QueryDSL 쿼리 결과에서 컴파일 타임에 타입 안전하게 DTO로 직접 매핑받기 위해 사용
    @QueryProjection
    public TodoSearchResponse(String title, Long managerCount, Long commentCount) {
        this.title = title;
        this.managerCount = managerCount;
        this.commentCount = commentCount;
    }
}
