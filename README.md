```markdown
# Resource Manager

Java Swing + JDBC + MySQL 기반 자원 관리 프로그램

---

## 🛠 개발 환경 및 사용 기술

- Java 17+
- Maven
- MySQL (MySQL Connector/J 9.4.0)
- Java Swing
- Git / GitHub

---

## 📁 프로젝트 구조

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

---

## 🗄 데이터베이스 설정

* **DB 이름:** `resource_manager`

### 1. DB 및 테이블 생성

MySQL 실행 후 프로젝트 루트의 `schema.sql`을 실행하여
`resource_manager` 데이터베이스와 테이블을 생성. (테스트 데이터 미포함)


### 2. DB 연결 설정

`util/DBConnection.java`에서 로컬 MySQL 환경에 맞게 접속 정보를 수정하고 연결을 테스트함.

```java
private static final String URL = "jdbc:mysql://localhost:3306/resource_manager";
private static final String USER = "root";
private static final String PASSWORD = "";

```

* `URL`: MySQL 서버 주소, 포트, DB 이름
* `USER`: MySQL 사용자 이름
* `PASSWORD`: MySQL 비밀번호

---

## 🚀 실행 순서

1. Maven 프로젝트를 불러오기.
2. `ProjectTest.java`를 실행하여 공통 환경 및 기반 기능을 점검.
3. 아래 메인 클래스를 실행하기. (실행 시 로그인 화면이 표시됨)

```text
com.team.resourcemanager.Main

---

## 📌 공통 상태값

* **권한:** `USER`, `ADMIN`
* **ITEM:** `AVAILABLE`, `MAINTENANCE`, `BORROWED`
* **LOAN:** `REQUESTED`, `BORROWED`, `REJECTED`, `RETURNED`, `OVERDUE`

---

## 🌿 Git 규칙

### 브랜치

- `main`: 안정 버전
- `feature/*`: 담당 기능 작업용 브랜치
  - 작업 완료 및 정상 동작 확인 후 PR 생성하여 `main`에 반영
  - `feature/common`: 공통 기능
  - `feature/login`: 로그인·회원 관리
  - `feature/item`: 물품·카테고리 관리
  - `feature/loan`: 대여 신청·승인
  - `feature/return`: 반납·연체
  - `feature/history`: 이력·일정·검색


### Commit 형식: `type: 작업내용`

* `feat`: 기능 추가
* `fix`: 버그 수정
* `ui`: 화면 수정
* `refactor`: 기능 변경 없이 코드 구조/정리
* `test`: 테스트 코드 및 테스트 작업
* `docs`: 문서 수정

```

```