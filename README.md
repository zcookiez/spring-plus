# SPRING PLUS (코드 개선 프로젝트)

본 프로젝트는 기존에 작성된 레거시 코드의 버그를 수정하고, 성능을 최적화하며, 최신 기술 스택으로 마이그레이션하는 리팩토링 과제를 수행한 결과물입니다.

---

## 🚀 과제 수행 내역 (Level 1 ~ 2)

### [Level 1] 버그 수정 및 기본 기능 강화
**1. `@Transactional` 버그 수정**
- 🧪 **Test File:** [1-transactional-test.http](./src/test/http/1-transactional-test.http)
- 할 일(Todo) 저장 시 발생하던 트랜잭션 롤백/에러 문제를 분석하고, `@Transactional` 어노테이션의 올바른 적용(readOnly 제거 등)을 통해 데이터가 정상적으로 DB에 저장되도록 수정했습니다.

**2. JWT 토큰 및 User 엔티티 닉네임 추가**
- 🧪 **Test File:** [2-nickname-test.http](./src/test/http/2-nickname-test.http)
- 사용자 엔티티(`User`)에 `nickname` 필드를 새롭게 추가하고, 회원가입 DTO 및 JWT 토큰의 Payload에도 닉네임 정보가 포함되도록 인증 로직을 확장했습니다.

**3. 할 일 검색(동적 쿼리) 기능 추가** 
- 🧪 **Test File:** [3-todo-search-test.http](./src/test/http/3-todo-search-test.http)
- 기획자의 요구사항에 맞춰, **날씨(weather)와 수정일 기간(startDate, endDate)**의 유무에 따라 유연하게 할 일을 검색하는 기능을 추가했습니다.
- 서비스 단에서 조건의 존재 여부를 분기(`if-else`)하여 각각에 맞는 JPQL 쿼리가 호출되도록 구현하였으며, 테스트 시 정확한 전체 건수(Count) 출력을 확인하기 위해 각 `@Query`에 명시적으로 `countQuery`를 추가했습니다.

**4. 컨트롤러 테스트(Controller Test) 코드 수정**
- 🧪 **Test File:** [TodoControllerTest.java](./src/test/java/org/example/expert/domain/todo/controller/TodoControllerTest.java)
- 에러 발생 시 잘못 검증하던 상태코드(`isOk()` -> `isBadRequest()`) 단언문(Assertion)을 올바르게 고쳤습니다.

### [Level 2] 성능 최적화 및 구조 개편
**5. 관리자 접근 로그 AOP 수정** 
- 🧪 **Test File:** [5-admin-aop-test.http](./src/test/http/5-admin-aop-test.http)
- 관리자 권한 변경 API 호출 시 작동해야 할 로그 AOP가 정상 작동하지 않던 문제를 해결하기 위해, 포인트컷 대상을 `UserAdminController`로 명확히 수정하고 시점을 `@Before`로 변경했습니다.

**6. JPA Cascade (영속성 전이) 적용** 
- 🧪 **Test File:** [6-cascade-test.http](./src/test/http/6-cascade-test.http)
- 할 일을 생성할 때 생성자를 자동으로 담당자(Manager)로 등록하기 위해 비즈니스 로직을 개선하고, `Todo` 엔티티의 `managers` 매핑에 `cascade = CascadeType.PERSIST` 속성을 부여하여 함께 영속화되도록 구현했습니다.

**7. 댓글(Comment) 조회 N+1 문제 해결**
- 🧪 **Test File:** [7-comment-nplus1-test.http](./src/test/http/7-comment-nplus1-test.http)
- 댓글 목록을 조회할 때 유저 정보를 반복해서 가져오며 발생하던 **N+1 문제**를 파악하고, `CommentRepository`의 조회 JPQL에 `JOIN FETCH` 문법을 적용하여 단일 쿼리로 최적화했습니다.

**8. 할 일(Todo) 조회 QueryDSL 마이그레이션** 
- 🧪 **Test File:** [8-querydsl-test.http](./src/test/http/8-querydsl-test.http)
- JPQL로 작성되어 있던 `findByIdWithUser` 메소드를 **QueryDSL** 환경으로 리팩토링했습니다.
- `QueryDslConfig`를 신규 구축하고, 연관 객체 조회를 위해 `.leftJoin().fetchJoin()`을 명시하여 향후 발생할 수 있는 N+1 문제를 원천 차단했습니다.

**9. Spring Security 전면 도입 (기존 Filter 교체)**
- 🧪 **Test File:** [9-spring-security-test.http](./src/test/http/9-spring-security-test.http)
- 수동으로 구현되어 있던 커스텀 `JwtFilter`와 `AuthUserArgumentResolver`를 모두 삭제하고, **Spring Security** 아키텍처로 마이그레이션했습니다.
- JWT 환경에 맞춰 세션(`STATELESS`)을 비활성화한 `SecurityConfig`를 구축하고, URL별 권한 제어(`hasAuthority("ADMIN")`) 로직을 적용했습니다.
- `JwtSecurityFilter`를 새로 구현하여 시큐리티 체인에 장착하고, 컨트롤러의 모든 `@Auth` 커스텀 어노테이션을 공식 `@AuthenticationPrincipal`로 일괄 교체했습니다.
