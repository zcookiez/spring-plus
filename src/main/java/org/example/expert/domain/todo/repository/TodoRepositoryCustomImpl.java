package org.example.expert.domain.todo.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.dto.response.QTodoSearchResponse;
import org.example.expert.domain.todo.dto.response.TodoSearchResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.example.expert.domain.todo.entity.QTodo.todo;
import static org.example.expert.domain.user.entity.QUser.user;
import static org.example.expert.domain.manager.entity.QManager.manager;
import static org.example.expert.domain.comment.entity.QComment.comment;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        Todo result = queryFactory
                .selectFrom(todo)
                .leftJoin(todo.user, user).fetchJoin()
                .where(todo.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<TodoSearchResponse> searchTodos(
            String keyword,
            LocalDate startDate,
            LocalDate endDate,
            String nickname,
            Pageable pageable
    ) {
        List<TodoSearchResponse> results = queryFactory
                .select(
                        // @QueryProjection으로 자동 생성된 Q-Class를 사용하면 파라미터 타입과 순서가 컴파일 시점에 검증되어 매우 안전함
                        new QTodoSearchResponse(
                                todo.title,
                                manager.id.countDistinct(),
                                comment.id.countDistinct()
                        )
                )
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .leftJoin(todo.comments, comment)
                .where(
                        titleContains(keyword),
                        createdAtGoe(startDate),
                        createdAtLoe(endDate),
                        nicknameContains(nickname)
                )
                .groupBy(todo.id)
                .orderBy(todo.modifiedAt.desc(), todo.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(todo.id.countDistinct())
                .from(todo)
                .leftJoin(todo.managers, manager)
                .leftJoin(manager.user, user)
                .where(
                        titleContains(keyword),
                        createdAtGoe(startDate),
                        createdAtLoe(endDate),
                        nicknameContains(nickname)
                );

        // 결과가 없거나 전체 데이터 수가 페이지 사이즈보다 작으면 불필요한 Count 쿼리를 생략하는 최적화 메서드
        return PageableExecutionUtils.getPage(results, pageable, countQuery::fetchOne);
    }

    private BooleanExpression titleContains(String keyword) {
        return StringUtils.hasText(keyword) ? todo.title.contains(keyword) : null;
    }

    // goe: Greater or Equal
    private BooleanExpression createdAtGoe(LocalDate startDate) {
        return startDate != null ? Expressions.dateTemplate(LocalDate.class, "DATE({0})", todo.createdAt).goe(startDate) : null;
    }

    // loe: Less or Equal
    private BooleanExpression createdAtLoe(LocalDate endDate) {
        return endDate != null ? Expressions.dateTemplate(LocalDate.class, "DATE({0})", todo.createdAt).loe(endDate) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return StringUtils.hasText(nickname) ? user.nickname.contains(nickname) : null;
    }
}
