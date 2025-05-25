# StudIT
스터디 매칭 플랫폼: 숙명여자대학교 인공지능공학부 학생들을 위한 스터디 그룹 관리 및 추천 시스템

---

## 🧩 프로젝트 개요

StudIT는 AI·IT 분야 학생들을 위해 설계된 스터디 매칭 플랫폼입니다.
학생은 자신의 관심 분야에 맞는 스터디를 생성하거나 추천받고, 그룹 내 일정을 함께 조율하며 효과적인 학습 환경을 만들어갈 수 있습니다.

- 🧠 관심 태그 기반 스터디 추천
- 📅 시간대 기반 일정 추천
- 📝 스터디 생성 및 지원/수락 기능
- 🔍 과목명 및 요일 기반 검색

---

## 🛠️ 기술 스택

- Language: Java 22
- UI: Java Swing
- Architecture: MVC 기반 구조
- Persistence: Object Serialization or JSON 저장

---

## 🗂️ 프로젝트 구조

```
C:\StudIT\src\studit
├─ domain               // 도메인 객체 (User, StudyGroup, Schedule 등)
├─ service              // 비즈니스 로직 (추천, 검색, 스터디 관리)
├─ ui                   // Swing 기반 UI 화면 (FrameManager, HomeFrame 등)
└─ Main.java            // 앱 진입점
```

---

## 🚀 실행 방법

1. 프로젝트를 IDE (IntelliJ, Eclipse 등)에서 열기
2. Main.java 실행
3. 회원가입 후 관심 태그를 설정하면 추천 스터디를 홈 화면에서 확인할 수 있습니다.

---

## ✨ 주요 기능 요약

| 기능 | 설명 |
|------|------|
| 회원가입 | 이름, 학번, 전공 입력 및 관심 태그 등록 |
| 스터디 생성 | 과목, 모임 방식, 태그, 최대 인원, 시간대 설정 |
| 스터디 추천 | 사용자의 관심 분야와 유사한 스터디 자동 추천 |
| 스터디 지원/수락 | 신청 버튼 → 리더가 수락 → 정원 채우기 |
| 일정 관리 | 스터디원 가능한 시간대 수집 및 추천 일정 생성 |
| 검색 기능 | 과목명 또는 요일로 필터링된 스터디 리스트 제공 |


---

## 📌 참고사항

- 데모 시 initializeDemoData()를 통해 예시 스터디 그룹 자동 생성 가능
- 추후 DB 또는 서버 연동 확장을 고려한 구조
- Java Swing 기반으로 GUI 중심의 로컬 실행 형태

---

## 📷 스크린샷 (업데이트 예정)

- 로그인/회원가입 화면
- 홈 화면 (추천 스터디 카드)
- 스터디 생성/검색/일정 조율 UI 등


---

## 🙋‍♀️ 팀원 소개

| 이름     | 이메일                         | GitHub ID     |
|----------|----------------------------------|---------------|
| 김예성   | xiviiiys02@sookmyung.ac.kr      | @xiviiiys0802 |
| 박서진   | newnew@sookmyung.ac.kr          | @sj2316351             |
| 유현서   | waieiches@sookmyung.ac.kr       | @waieiches    |
| 이유정   | uujeong59@sookmyung.ac.kr       | @uujeonglee   |
| 장규민   | 1104_cgm@sookmyung.ac.kr        | @gyu1104      |
| 한지수   | 2011661@sookmyung.ac.kr         | @isuHan       |