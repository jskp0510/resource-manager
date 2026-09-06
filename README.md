# Resource Manager

Java Swing + JDBC + MySQL 기반 자원 관리 프로그램입니다.

## 개발 환경

- Java 17+
- Maven
- MySQL 8.x
- MySQL Connector/J 9.4.0
- Java Swing
- BCrypt

## 프로젝트 구조

```text
src/main
├── java/com/team/resourcemanager
│   ├── model
│   ├── dao
│   ├── service
│   ├── ui
│   └── util
└── resources
```

## 데이터베이스 설정

MySQL Workbench에서 프로젝트 루트의 `schema.sql`을 실행합니다.

```sql
USE resource_manager;
SHOW TABLES;
```

DB 비밀번호는 코드에 저장하지 않습니다. Windows 사용자 환경변수에 아래 값을 등록합니다.

```text
변수 이름: RESOURCE_MANAGER_DB_PASSWORD
변수 값: 로컬 MySQL root 비밀번호
```

필요한 경우 다음 환경변수로 기본값을 바꿀 수 있습니다.

```text
RESOURCE_MANAGER_DB_URL
RESOURCE_MANAGER_DB_USER
```

기본 접속값은 `jdbc:mysql://localhost:3306/resource_manager`, `root`입니다.

## 빌드와 테스트

```cmd
mvn clean test
```

`RESOURCE_MANAGER_DB_PASSWORD`가 설정되어 있고 DB가 실행 중이면 USER 테이블 CRUD 통합 테스트도 실행됩니다. 환경변수가 없으면 해당 통합 테스트만 건너뜁니다.

## 실행

VS Code에서 `com.team.resourcemanager.Main`을 실행하거나 다음 명령을 사용합니다.

```cmd
mvn exec:java -Dexec.mainClass=com.team.resourcemanager.Main
```

## 회원가입 규칙

- 아이디: 영문, 숫자, 밑줄 4~20자
- 비밀번호: 8자 이상, 문자와 숫자를 각각 1개 이상 포함
- 이름: 2~20자
- 가입 계정의 기본 권한: `USER`
- 비밀번호는 BCrypt 해시로만 저장

## 테스트 계정 준비

1. 회원가입 화면에서 일반 계정과 관리자용 계정을 각각 가입합니다.
2. MySQL Workbench에서 관리자용 계정의 권한만 변경합니다.

```sql
USE resource_manager;
UPDATE `USER`
SET role = 'ADMIN'
WHERE login_id = 'admin01';
```

3. 결과를 확인합니다.

```sql
SELECT user_id, login_id, name, role
FROM `USER`;
```

비밀번호 컬럼은 평문이 아닌 `$2a$...` 형태의 BCrypt 해시여야 합니다.

## 권한 기준

- `USER`: 대시보드, 대여 관리, 반납 관리, 이력 조회, 일정
- `ADMIN`: USER 메뉴 + 물품 관리 + 회원 관리

화면에서 관리자 메뉴를 숨기며, 관리자 메뉴 이벤트에서도 권한을 다시 확인합니다.

## Git 규칙

- `main`: 안정 버전
- `feature/login`: 로그인·회원 관리
- 커밋 형식: `type: 작업내용`
- 기능 검증 후 Pull Request로 `main`에 반영

DB 비밀번호, 환경변수 값, 개인 설정 파일은 커밋하지 않습니다.
