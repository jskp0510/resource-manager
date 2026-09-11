```markdown
# Resource Manager

Java Swing + JDBC + MySQL 기반 자원 관리 프로그램

---

## 개발 환경 및 사용 기술

- Java 17+
- Maven
- MySQL (MySQL Connector/J 9.4.0)
- Java Swing
- Git / GitHub

---

## 프로젝트 구조

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

## 구현 기능

- 일반 사용자 대여 신청
- 대여 가능한 물품 조회
- 동일 물품 중복 신청 방지
- 관리자 승인 대기 목록 조회
- 관리자 대여 승인
- 관리자 대여 거절
- 승인 시 LOAN과 ITEM 상태를 트랜잭션으로 함께 변경

### 물품 선택

사용자가 DB 내부의 `item_id`를 직접 입력하지 않도록 구현

`AVAILABLE` 상태이면서 현재 `REQUESTED` 신청이 없는 물품만
대여 신청 화면에 표시

화면에는 물품명과 시리얼 번호를 표시하고,
DB 처리에서는 내부적으로 `item_id`를 사용

---

## 대여 상태 전환

- 대여 신청: LOAN은 `REQUESTED`, ITEM은 `AVAILABLE`
- 관리자 승인: LOAN과 ITEM 모두 `BORROWED`
- 관리자 거절: LOAN은 `REJECTED`, ITEM은 `AVAILABLE`

관리자 승인 시 LOAN과 ITEM의 상태 변경은 하나의 트랜잭션으로 처리

---

## 주요 클래스

- `Loan`: 대여 정보
- `LoanDAO`: 대여 관련 DB 조회 및 변경
- `LoanService`: 대여 신청 검증 및 승인·거절 처리
- `LoanRequestPanel`: 사용자 대여 신청 화면
- `LoanAdminPanel`: 관리자 승인·거절 화면

---

## 테스트
```sql
SELECT user_id, login_id, name, role
FROM `USER`;
```

관리자 계정이 필요한 경우 해당 계정의 권한을 변경

```sql
UPDATE `USER`
SET role = 'ADMIN'
WHERE login_id = 'admin01';
```

카테고리 확인

```sql
SELECT *
FROM CATEGORY;
```

`category_id`를 사용하여
`AVAILABLE` 상태의 물품 등록

```sql
INSERT INTO ITEM
(category_id, item_name, serial_no, description, status)
VALUES
(1, '충전기', 'CHARGER-001', '수업 사용', 'AVAILABLE');
```

대여 신청 후 최근 신청 내역을 확인

```sql
SELECT *
FROM LOAN
ORDER BY loan_id DESC;
```

물품 상태 확인

```sql
SELECT item_id, item_name, serial_no, status
FROM ITEM;
```

대여 신청 시 LOAN은 `REQUESTED`,
승인 시 LOAN과 ITEM은 `BORROWED`,
거절 시 LOAN은 `REJECTED`, ITEM은 `AVAILABLE` 상태인지 확인

---

## 참고사항

- `LoanRequestPanel`을 사용자 대여 메뉴에 연결
- `LoanAdminPanel`을 관리자 대여 관리 메뉴에 연결
- 로그인 기능과 현재 사용자 정보 및 권한 연결
- 공통 DBConnection 구조와 연결