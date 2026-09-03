```markdown
# Resource Manager

Java Swing + JDBC + MySQL 기반 자원 관리 프로그램

## 개발 환경

- Java 17+
- Maven
- MySQL
- MySQL Connector/J 9.4.0

## 사용 기술 및 도구

- Java Swing
- JDBC
- MySQL
- Git / GitHub

## 프로젝트 구조


src/main/java/com/team/resourcemanager
├── model
├── dao
├── service
├── ui
└── util


## 데이터베이스

DB 이름:

```text
resource_manager
```

### DB 생성 및 테이블 생성

MySQL을 실행한 후 `resource_manager` 데이터베이스를 생성.

```sql
CREATE DATABASE resource_manager;
USE resource_manager;
```

이후 프로젝트 루트의 `schema.sql`을 실행하여 테이블을 생성.

> `schema.sql`에는 테스트 데이터가 포함되어 있지 않음.

### DB 연결

DB 연결은 `util/DBConnection.java`에서 관리.

각자의 로컬 MySQL 환경에 맞게 `DBConnection.java`의 DB 접속 정보를 설정.

```java
private static final String URL = "jdbc:mysql://localhost:3306/resource_manager";
private static final String USER = "root";
private static final String PASSWORD = "";
```

* `URL`: MySQL 서버 주소, 포트, DB 이름
* `USER`: MySQL 사용자 이름
* `PASSWORD`: MySQL 비밀번호

설정 후 DB 연결 테스트를 실행하여 정상적으로 연결되는지 확인.

## 실행

1. MySQL을 실행.
2. `resource_manager` 데이터베이스를 생성하고 `schema.sql`을 실행.
3. `DBConnection.java`의 DB 접속 정보를 본인 환경에 맞게 설정.
4. Maven 프로젝트를 불러옴.
5. 다음 클래스를 실행.

```text
com.team.resourcemanager.Main
```

실행 시 로그인 화면이 표시됨.



## 공통 상태값

### 권한

```
USER
ADMIN
```

### ITEM

```
AVAILABLE
MAINTENANCE
BORROWED
```

### LOAN

```
REQUESTED
BORROWED
REJECTED
RETURNED
OVERDUE
```

## Git 규칙

- `main`: 안정 버전
- `feature/*`: 담당 기능 작업용 브랜치
    feature/common : 공통 기능
    feature/login : 로그인·회원 관리
    feature/item : 물품·카테고리 관리
    feature/loan : 대여 신청·승인
    feature/return : 반납·연체
    feature/history : 이력·일정·검색
- 기본적인 작업은 `feature/*` 브랜치에서 진행
- 작업이 완료되고 정상 동작을 확인한 후 Pull Request를 생성
- Pull Request 확인 후 `main`에 반영
- Commit 형식: `type: 작업내용`

```
feat: 기능 추가
fix: 버그 수정
ui: 화면 수정
refactor: 기능 변경 없이 코드 구조/정리
test: 테스트 코드 및 테스트 작업
docs: 문서 수정
```