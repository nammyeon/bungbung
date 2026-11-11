# 붕붕 – 운전하는 대로 자라는 내 차 캐릭터

현대자동차그룹 차량용 앱 개발 공모전 출품작 (Pleos Connect / Android Automotive)

## 실행 방법

1. Android Studio에서 **File → Open**으로 이 폴더(`bungbung-app`)를 연다.
2. 처음 열면 Gradle Sync가 자동으로 돈다(몇 분 걸림). "Upgrade" 안내가 뜨면 그냥 닫아도 된다.
3. 에뮬레이터를 고른다.
   - Pleos Connect 에뮬레이터가 있으면 그걸 쓴다.
   - 없으면 Device Manager에서 **Automotive** 기기(가로 화면)를 만들어도 되고, 일반 휴대폰 에뮬레이터에서도 실행된다.
4. 초록색 ▶ Run 버튼을 누른다.
5. 처음 실행하면 알림 권한을 묻는다. **허용**해야 상단 알림 팝업이 뜬다.

APK 만들기: **Build → Build App Bundle(s) / APK(s) → Build APK(s)** → `app/build/outputs/apk/debug/app-debug.apk`

## 조작 방법

캐릭터 키우기 게임처럼 붕붕이 방 화면이 기본이에요.

- **위쪽**: 붕붕이 얼굴, 함께한 날(D+n), 성격, 레벨 하트와 경험치, 배부름·친밀도 게이지
- **왼쪽 동그라미**: 상태(성격·누적 거리·도감 수), 시연 도구(3일 뒤로 가기, 처음부터 다시)
- **아래 메뉴**: 간식 · 쓰다듬기 · 가운데 열쇠(시동 걸기 → 출발 → 시동 끄기) · 도감 · 주유
- 붕붕이를 직접 눌러도 쓰다듬기가 돼요.
- 간식·쓰다듬기·도감·주유는 주차(P) 중에만 돼요. 주행 중에는 화면 조작이 잠기고 음성·상단 알림만 나와요.

## 시연 순서 (시연 영상용, 약 3분)

1. **시연 → 처음부터 다시** → **열쇠** → "주말 강릉 여행" 고르기 → 붕붕이 인사 → **출발**
   - 서울·하남·양평·횡성·평창·강릉에 들어갈 때마다 상단 알림 팝업 + 음성 ("강릉 입성! 커피잔 획득!")
   - 도착(자동 주차) → 오늘의 기록 창, 레벨 업
2. **도감** → 커피잔 눌러서 착용 → 붕붕이 머리에 커피잔
3. **간식 / 쓰다듬기** (주차 중에만 가능)
4. **시동 끄기** → **열쇠** → "출근길 · 급정거 많음" → 출발
   - 급정거마다 "으악! 깜짝이야…" → 기록에서 겁이 늘어남
5. 다시 시동 → 붕붕이가 "어제 급정거 3번… 나 좀 무서웠어"라고 기억해서 말함
6. "차량 관리 · 타이어 공기압 낮음" → 시동 → "발이 좀 이상해…" (아픈 표정)
7. "차량 관리 · 연료 부족" → 배고픈 표정 → 주차 후 **주유** → "배불러!"
8. 시동 끈 상태에서 **시연 → 3일 뒤로 가기** → 시동 → "흥, 3일 동안 어디 갔었어?" (삐짐)

## 구조

```
app/src/main/java/com/bungbung/
├── core/                      ← 안드로이드와 상관없는 순수 로직 (단위 테스트 있음)
│   ├── Vehicle.kt             차량 상태(속도·기어·주행거리·연료·타이어·경고·위치)와 VehicleSource 인터페이스
│   ├── DrivingAnalyzer.kt     속도 변화량으로 급제동·급가속 판단, 주행 요약
│   ├── Regions.kt             지역 도감 데이터와 현재 지역 판정
│   ├── CharacterEngine.kt     성격·레벨·친밀도·배부름·컨디션·기분 계산
│   ├── Dialogue.kt            붕붕이 대사
│   └── DriveScenario.kt       시연용 가짜 주행 시나리오
└── app/                       ← 안드로이드 화면과 기능
    ├── MainActivity.kt
    ├── BungViewModel.kt       시동 → 인사 → 주행 → 주차 흐름
    ├── SimulatedVehicleSource.kt  시나리오를 실시간처럼 흘려보냄
    ├── Notifier.kt            상단 알림 팝업
    ├── Speaker.kt             음성(TTS)
    ├── BungStore.kt           붕붕이 상태 저장(기기 안에만)
    └── ui/
        ├── GameScreens.kt     방 화면·주행 화면·도감·상태·기록 창
        ├── ArtDraw.kt         그림 그리는 함수, 성격별 몸 색
        └── Art.kt             손그림 캐릭터·배경·아이콘 데이터 (tools/art_gen.py로 생성)
```

### 판단 기준

| 항목 | 기준 |
|---|---|
| 급제동 | 1초에 9 km/h 이상 감속 (15 km/h 이상 달리던 중) |
| 급가속 | 1초에 11 km/h 이상 가속 |
| 성격 | 부드러운 주행 +4, 급제동 1회 −4, 급가속 1회 −3 (−100 겁 많음 ~ +100 다정함) |
| 경험치 | 1 km = 10 경험치. 레벨 n에 필요한 누적 경험치 = 50·n·(n−1) |
| 삐짐 | 3일 이상 안 타면 삐지고 친밀도가 줄어듦 |
| 컨디션 | 차량 경고 → 아픔, 타이어 이상 → 발 아픔, 연료 15% 미만 → 배고픔 |

### 실제 차량(Pleos SDK) 연결 지점

지금은 에뮬레이터에 실제 주행이 없어서 `SimulatedVehicleSource`가 가짜 주행 데이터를 만든다.
실차에서는 `VehicleSource` 인터페이스를 구현하는 클래스 하나만 추가하면 된다.

- Vehicle SDK: 속도, 기어(P/D), 주행거리(Odometer), 연료/배터리, 타이어, 경고 → `VehicleSnapshot`
- FusedLocation SDK: 위도·경도 → `VehicleSnapshot.lat/lon`
- TextToSpeech SDK: `Speaker.speak()` 안을 Pleos TTS 호출로 교체

## 테스트

`app/src/test/java/com/bungbung/core/CoreTest.kt` — Android Studio에서 파일을 열고 ▶ 버튼으로 실행.

## 사용한 글꼴

[주아체(Jua)](https://github.com/google/fonts/tree/main/ofl/jua) – SIL Open Font License 1.1 (`licenses/Jua-OFL.txt`)
