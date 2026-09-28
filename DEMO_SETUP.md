# 시연용 계정 및 샘플 데이터

## 계정

| 역할 | 로그인 ID | 비밀번호 |
|---|---|---|
| 일반 사용자 | `demo_user` | `DemoUser2026!` |
| 관리자 | `demo_admin` | `DemoAdmin2026!` |

이 계정은 시연용 DB 전용이다. 클라우드에는 시연 데이터만 넣고, DB 사용자 권한과 네트워크 접근 범위를 제한한다.

## 현재 시연 DB

현재 시연용 Aiven DB에는 이 계정과 샘플 데이터가 준비되어 있다. 실행할 때 `schema.sql`이나 `demo_seed.sql`을 다시 적용하지 않는다.

## 새 DB를 만들 때만 수행할 데이터 준비

1. 비어 있는 새 `resource_manager` 데이터베이스에 `schema.sql`을 한 번 적용한다.
2. `demo_seed.sql`을 적용한다.
3. 앱을 실행해 두 계정으로 로그인한다.

`schema.sql`은 테이블을 삭제한 뒤 다시 생성하므로 기존 데이터가 있는 DB에서 실행하지 않는다. `demo_seed.sql`은 기존 행을 삭제하지 않고, 일치하는 시연 레코드가 없을 때만 추가한다. 이미 존재하는 시연 레코드의 상태는 초기화하지 않는다.

## 샘플 데이터 구성

| 일련번호 | 시작 상태 | 연결 LOAN 상태 | 확인 용도 |
|---|---|---|---|
| `DEMO-ITEM-001` | `AVAILABLE` | 없음 | 새 대여 신청 및 전체 흐름 시연 |
| `DEMO-ITEM-002` | `BORROWED` | `BORROWED` | 반납 신청 시연 |
| `DEMO-ITEM-003` | `BORROWED` | `OVERDUE` | 연체 목록 및 일수 표시 |
| `DEMO-ITEM-004` | `AVAILABLE` | `REQUESTED` | 관리자 대여 승인 대기 |
| `DEMO-ITEM-005` | `BORROWED` | `RETURN_REQUESTED` | 관리자 반납 확인 대기 |
| `DEMO-ITEM-006` | `AVAILABLE` | `RETURNED` | 완료된 대여 이력 |

대여 시작일·예정일·반납일은 SQL 실행 날짜를 기준으로 상대 날짜로 입력된다.

## 시연 흐름

##자세한것은 readme.md참고.

1. `demo_user`로 로그인해 `DEMO-ITEM-001` 대여를 신청한다.
2. `demo_admin`으로 로그인해 해당 신청을 승인한다.
3. `demo_user`로 로그인해 대여 물품의 반납을 신청한다.
4. `demo_admin`으로 로그인해 반납을 확인한다.
5. `demo_user`의 본인 이력과 `demo_admin`의 전체 이력·일정에서 변경 결과를 확인한다.

위 초기화 절차는 새 DB를 만들 때만 사용한다. 기존 시연 DB에 `schema.sql`을 다시 적용하지 않는다.
