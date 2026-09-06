# 로그인·회원 관리 적용 가이드

## 1. 적용 전 확인

기존 프로젝트에서 다음을 실행합니다.

```cmd
cd /d C:\dev\resource-manager
git switch feature/login
git status
```

`nothing to commit, working tree clean` 상태에서 적용하는 것이 가장 안전합니다.

## 2. 완성본 복사

완성본 ZIP의 압축을 별도 폴더에 풉니다. 압축 안의 아래 항목을 기존 `C:\dev\resource-manager`로 복사하고 같은 이름의 파일은 교체합니다.

```text
pom.xml
README.md
handoff.md
LOGIN_IMPLEMENTATION_GUIDE.md
src
```

기존 프로젝트의 `.git` 폴더는 삭제하거나 교체하지 않습니다.

## 3. 추가·수정 파일

### 새 파일

```text
src/main/java/com/team/resourcemanager/dao/UserDAO.java
src/main/java/com/team/resourcemanager/service/AuthService.java
src/main/java/com/team/resourcemanager/service/AuthenticationException.java
src/main/java/com/team/resourcemanager/service/ValidationException.java
src/main/java/com/team/resourcemanager/ui/SignUpFrame.java
src/main/java/com/team/resourcemanager/util/PasswordUtil.java
src/main/java/com/team/resourcemanager/util/Session.java
src/test/java/com/team/resourcemanager/dao/UserDAOIntegrationTest.java
src/test/java/com/team/resourcemanager/service/AuthServiceTest.java
src/test/java/com/team/resourcemanager/util/PasswordUtilTest.java
src/test/java/com/team/resourcemanager/util/SessionTest.java
```

### 수정 파일

```text
pom.xml
src/main/java/com/team/resourcemanager/model/User.java
src/main/java/com/team/resourcemanager/ui/LoginFrame.java
src/main/java/com/team/resourcemanager/ui/MainFrame.java
src/main/java/com/team/resourcemanager/util/DBConnection.java
README.md
handoff.md
```

## 4. Maven 새로고침

VS Code에서 `Ctrl + Shift + P`를 누르고 다음을 실행합니다.

```text
Java: Clean Java Language Server Workspace
```

재시작 확인을 누릅니다. BCrypt와 JUnit 의존성은 Maven이 `pom.xml`을 보고 받습니다.

## 5. 자동 테스트

MySQL80 서비스가 실행 중이고 `RESOURCE_MANAGER_DB_PASSWORD`가 등록된 상태에서 실행합니다.

```cmd
mvn clean test
```

성공 기준:

```text
BUILD SUCCESS
```

통합 테스트는 임시 사용자를 생성하고 CRUD를 검사한 뒤 자동 삭제합니다.

## 6. 프로그램 실행

```cmd
mvn exec:java
```

또는 VS Code에서 `Main.java`의 `Run Java`를 누릅니다.

## 7. 기능 확인

### 회원가입

1. 로그인 화면에서 `회원가입` 클릭
2. `user01`, 문자·숫자를 포함한 8자 이상 비밀번호, 이름 입력
3. 가입 성공 확인
4. 같은 ID로 재가입하여 중복 오류 확인

DB 확인:

```sql
SELECT user_id, login_id, password, name, role
FROM `USER`;
```

`password`는 입력한 값이 아니라 `$2a$...` 형태여야 하며 `role`은 `USER`여야 합니다.

### 관리자 계정

회원가입으로 `admin01`을 만든 뒤 Workbench에서 실행합니다.

```sql
UPDATE `USER`
SET role = 'ADMIN'
WHERE login_id = 'admin01';
```

`user01` 로그인 시 `물품 관리`, `회원 관리`가 보이지 않아야 합니다. `admin01` 로그인 시 두 메뉴가 보여야 합니다.

### 로그인·로그아웃

- 잘못된 ID와 비밀번호는 동일한 실패 메시지를 표시해야 합니다.
- 로그인 성공 후 사용자 이름과 권한이 우측 상단에 보여야 합니다.
- 로그아웃 확인에서 `아니오`를 누르면 메인 화면을 유지해야 합니다.
- `예`를 누르면 세션이 초기화되고 로그인 화면으로 돌아가야 합니다.

### DB 오류

MySQL80 서비스를 잠시 중지한 뒤 로그인 또는 회원가입을 시도합니다. 프로그램이 종료되지 않고 DB 실행 상태 확인 메시지가 나와야 합니다. 검사 후 MySQL80 서비스를 다시 시작합니다.

## 8. Git 저장

비밀번호가 포함되지 않았는지 먼저 확인합니다.

```cmd
git status
git diff
```

기능별 커밋을 권장합니다.

```cmd
git add pom.xml src/main/java/com/team/resourcemanager/dao src/main/java/com/team/resourcemanager/service src/main/java/com/team/resourcemanager/util/PasswordUtil.java src/main/java/com/team/resourcemanager/util/Session.java src/main/java/com/team/resourcemanager/model/User.java
git commit -m "feat: 사용자 DAO와 인증 서비스 구현"

git add src/main/java/com/team/resourcemanager/ui/LoginFrame.java src/main/java/com/team/resourcemanager/ui/SignUpFrame.java src/main/java/com/team/resourcemanager/ui/MainFrame.java
git commit -m "feat: 회원가입 로그인 권한 로그아웃 구현"

git add src/test README.md handoff.md LOGIN_IMPLEMENTATION_GUIDE.md
git commit -m "test: 로그인 회원관리 테스트와 인계 문서 추가"

git push origin feature/login
git status
```

최종 상태는 `nothing to commit, working tree clean`이어야 합니다.

## 9. Pull Request

GitHub의 `feature/login` 브랜치에서 `Compare & pull request`를 누르고 대상이 `main`인지 확인합니다. PR 설명에는 회원가입, BCrypt, 로그인, Session, 권한, 로그아웃, 테스트 완료를 기록합니다.
