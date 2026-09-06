# 로그인·회원 관리 작업 인계 메모

## 구현 완료 기능

- `User` 모델 및 비밀번호 제거 복사본
- `UserDAO` USER 테이블 CRUD
- PreparedStatement 및 try-with-resources 적용
- BCrypt 비밀번호 해시·비교
- 회원가입 입력 검증과 ID 중복 검사
- DB 유니크 제약 경합 상황 중복 처리
- DB 기반 로그인과 일반화된 실패 메시지
- Singleton `Session`
- 세션 내 비밀번호 제거
- USER/ADMIN 메뉴 접근 구분과 재검사
- 로그아웃 확인 및 세션 초기화
- DB 작업 중 버튼 중복 클릭 방지
- DB 응답 중 Swing 화면 멈춤 방지를 위한 SwingWorker
- 자동 단위 테스트 및 로컬 DB CRUD 통합 테스트

## 주요 클래스

```text
model/User.java
dao/UserDAO.java
service/AuthService.java
service/ValidationException.java
service/AuthenticationException.java
util/PasswordUtil.java
util/Session.java
ui/LoginFrame.java
ui/SignUpFrame.java
ui/MainFrame.java
```

현재 사용자는 다음 코드로 확인합니다.

```java
User currentUser = Session.getInstance().getUser();
```

권한은 다음 코드로 확인합니다.

```java
boolean admin = Session.getInstance().isAdmin();
```

## 다음 담당자 주의사항

- 로그인 전 `MainFrame`을 직접 생성하지 않습니다.
- Session에는 평문 비밀번호와 비밀번호 해시를 저장하지 않습니다.
- 관리자 기능은 버튼 표시 여부와 별도로 이벤트/서비스에서도 다시 권한을 확인합니다.
- `USER`, `ADMIN` 문자열은 `Constants`를 사용합니다.
- SQL 문자열 연결 대신 `PreparedStatement`를 사용합니다.
- 실제 DB 비밀번호를 코드·README·커밋에 작성하지 않습니다.
- 다른 기능 화면을 연결할 때 기존 로그아웃과 Session 처리를 유지합니다.

## 완료 확인 순서

1. `mvn clean test` 성공
2. 회원가입 성공 및 DB 행 생성
3. 동일 ID 재가입 차단
4. DB `password`가 BCrypt 해시인지 확인
5. 틀린 ID/PW 로그인 차단
6. USER 로그인 후 관리자 메뉴 미표시
7. ADMIN 로그인 후 관리자 메뉴 표시
8. 로그아웃 후 Session 초기화 및 로그인 화면 복귀
9. MySQL 중지 상태에서 사용자 친화적인 DB 오류 메시지 확인

## 테스트 계정

회원가입으로 `user01`, `admin01` 등의 계정을 만든 뒤 관리자 계정만 Workbench에서 승격합니다.

```sql
UPDATE `USER`
SET role = 'ADMIN'
WHERE login_id = 'admin01';
```

비밀번호는 Git과 인계 문서에 기록하지 않고 팀 내부의 안전한 방법으로 전달합니다.
