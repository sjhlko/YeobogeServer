![readme](https://github.com/HeyBoardgame/server/assets/67736320/836646ec-75c9-470b-8fca-5c270fea9603)

## 🚩 Table of Contents

- [About The Project](#-about-the-project)
- [Tech Stack](#-tech-stack)
- [Key Features](#-key-features)
- [Contribution](#-contribution)
- [Docs](#-docs)

## 🔖 About The Project
<table>
<tr>
<td>
    보드게임 카페에서나 웹사이트 등에서 찾아 볼 수 있는 기존 보드게임 추천 서비스들은 대부분 단순 인기순으로 추천하거나, 사용자가 직접 복잡한 질문에 답해야 추천을 받을 수 있습니다.
    여보게는 이런 점을 보완하여 사용자의 취향에 맞는 보드게임들을 자동으로 추천해주는 서비스입니다. <br><br>
    여보게에서는 OTT 서비스처럼 나만을 위해 AI가 추천한 보드게임을 카테고리 별로 볼 수 있습니다.
    또한 같은 곳에 있는 친구들과 그룹을 맺어 우리를 위한 보드게임을 추천받을 수도 있습니다.
</td>
</tr>
</table>

> 2023 홍익대학교 컴퓨터공학과 창직종합설계프로젝트 \
> 개발 기간: 2023.07 ~ 2023.11

### API URL
http://13.125.211.203:8080/api/v1

## 🌟 Tech Stack

### Environment
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white"> <img src="https://img.shields.io/badge/gitlab-FC6D26?style=for-the-badge&logo=gitlab&logoColor=white"> <img src="https://img.shields.io/badge/intellij-000000?style=for-the-badge&logo=intellijidea&logoColor=white">

### Development
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white"> <img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> <img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white"> <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">

### DevOps
<img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white"> <img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white"> <img src="https://img.shields.io/badge/gitlab cicd-FC6D26?style=for-the-badge&logo=gitlab&logoColor=white">

### Communication
<img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white"> <img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">

## ✨ Key Features

### 개인 혹은 그룹 맞춤 보드게임 추천 기능
- **"친구들이 좋아하는 보드게임"**, **"내가 좋아하는 특정 장르의 추천 보드게임"** 등 다양한 카테고리 별 추천 보드게임 목록을 제공 받을 수 있습니다.
  - [AI 기반 추천 목록 생성 API](https://github.com/HeyBoardgame/recommender): Spring WebClient로 API를 비동기 요청해 응답 시간을 줄였습니다.
  - **"친구들이 선호하는"**, **"그룹으로 추천 받았던"** 등 DB에서 조회된 데이터: `CompletableFuture`를 통해 DB 조회 쿼리도 비동기로 동작하도록 해 응답 시간을 줄였습니다.
- 친구로 등록된 사용자들과 자동으로 **그룹 매칭**을 받을 수 있습니다.
  - GPS 값이 일치하는 사용자들을 `ConcurrentHashMap`에 저장한 뒤, 같은 GPS에 위치한 사용자 중 매칭을 요청한 사용자와 친구 관계인 사용자들만 필터링해 그룹 매칭 결과를 응답합니다.
- **그룹**으로 매칭된 사용자들의 취향이 모두 반영된 **추천 보드게임** 목록을 제공 받을 수 있습니다.
  - 개인 추천과 동일하게 [외부 API](https://github.com/HeyBoardgame/recommender)를 비동기로 요청한 뒤 응답 결과를 토대로 DB에서 필요한 데이터를 가공해 최종 추천 결과를 응답합니다.

### 친구 관리 및 채팅 기능
- 다른 사용자에게 **친구 요청**을 보내고, 받은 요청을 **수락**하거나 **거절**할 수 있습니다.
- 친구로 등록된 사용자와 **실시간으로 1:1 채팅**을 주고 받을 수 있습니다.
  - Spring STOMP를 통해 사용자 간 채팅방을 `pub/sub` 기반 별도의 구독 엔드포인트로 관리하고, 해당 엔드포인트를 구독한 사용자들이 실시간으로 메세지를 송수신합니다.
  - 특정 채팅방 엔드포인트에 대해 현재 구독하고 있는 클라이언트 수를 토대로 새로운 메세지가 전송될 때마다 커스텀 헤더를 설정해 '읽음 여부'를 함께 전달합니다.
- **새로운 채팅이 온 채팅방**이 실시간으로 업데이트 되며, 각 채팅방마다 **안 읽은 메세지 수**를 확인할 수 있습니다.
  - `SSE`를 기반으로 구현 되었으며, 서버로 새로운 메세지 전송 요청이 오면 메세지 수신자에게 해당 이벤트를 전송합니다.
- 새로운 채팅 메세지 혹은 친구 요청 관련 **푸시 알림**이 전송됩니다.
  - 클라이언트로부터 전달 받은 모바일 기기의 FCM 토큰을 `Redis`에 저장해두고 해당 토큰으로 FCM API를 요청해 푸시 알림을 전송합니다.

### 보드게임 관련 기능
- 키워드를 통해 찾고 싶은 보드게임을 **검색**하고, **장르나 인원수 필터**를 적용해 더 구체적인 검색 결과를 얻을 수 있습니다.
  - 복잡한 쿼리가 필요한 검색에 대해 `QueryDsl`을 통해 동적 조회 쿼리 기반 페이징을 구현했습니다.
- 특정 보드게임의 **상세 정보를 조회**하고, 그 보드게임에 대한 **다른 친구의 평가**를 확인할 수 있습니다.
  - 상세 조회한 보드게임을 평가한 친구가 있다면 3.5를 기준으로 가장 높은 별점을 준 친구와 낮은 별점을 준 친구를 함께 전달합니다.
  - 평가를 남긴 친구에 대해 해당 친구와의 채팅방 ID를 요청해 실시간 채팅으로 바로 연결 가능합니다.
- 특정 보드게임을 **찜**하거나 **별점**을 남길 수 있습니다.
- **찜하거나, 평가한 보드게임**들을 **조회**할 수 있습니다.
  - 커스텀 `PageRequest`를 구현해 '난이도순(쉬운/어려운)', '인기순' 등 별도의 쿼리가 필요한 정렬 기준을 설정할 수 있습니다.

## Contribution

### Member

|                                                                서정희                                                                |                                                               윤수빈                                                                |
|:---------------------------------------------------------------------------------------------------------------------------------:|:--------------------------------------------------------------------------------------------------------------------------------:|
| [<img src="https://avatars.githubusercontent.com/u/76868151?v=4" height=150 width=150> <br/> @sjhlko](https://github.com/sjhlko) | [<img src="https://avatars.githubusercontent.com/u/67736320?v=4" height=150 width=150> <br/> @s0o0bn](https://github.com/s0o0bn) |

### Developments

**서정희**

- **인증** : FCM 토큰 등록, 임시 비밀번호 발급, 비밀번호 수정, 회원 탈퇴
- **소셜** : 친구 요청, 요청 수락 및 거절, 채팅 관련 전 기능
- **보드게임** : 보드게임 검색, 상세 조회
- **프로필** : 프로필 조회 및 수정

**윤수빈**

- **인증** : 회원 가입, 로그인 및 로그아웃, 이메일 및 닉네임 중복 확인, 토큰 리프레시, 구글 로그인
- **소셜** : 친구 및 친구 요청 조회
- **추천** : 개인 및 그룹 추천 관련 전 기능
- **보드게임** : 보드게임 찜하기 및 평가하기, 찜하거나 평가한 보드게임 조회

## 📑 Docs

- [API Documentation](https://documenter.getpostman.com/view/19369137/2s9Xy6rqXr)
- [ER Diagram](https://www.erdcloud.com/d/ciXutL5fA6iMEyBuy)
