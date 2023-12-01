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
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
<img src="https://img.shields.io/badge/gitlab-FC6D26?style=for-the-badge&logo=gitlab&logoColor=white">
<img src="https://img.shields.io/badge/intellij-000000?style=for-the-badge&logo=intellijidea&logoColor=white">

### Development
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/spring boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">

### DevOps
<img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white">
<img src="https://img.shields.io/badge/docker-2496ED?style=for-the-badge&logo=docker&logoColor=white">
<img src="https://img.shields.io/badge/gitlab cicd-FC6D26?style=for-the-badge&logo=gitlab&logoColor=white">

### Communication
<img src="https://img.shields.io/badge/discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">
<img src="https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white">

## ✨ Key Features

### 개인 혹은 그룹 맞춤 보드게임 추천 기능
- 친구들이 좋아하는 보드게임, 내가 좋아하는 특정 장르의 추천 보드게임 등 다양한 카테고리 별 추천 보드게임 목록을 제공 받을 수 있습니다.
- 친구로 등록된 사용자들과 자동으로 그룹 매칭을 받을 수 있습니다.
- 그룹으로 매칭된 사용자들의 취향이 모두 반영된 추천 보드게임 목록을 제공 받을 수 있습니다.

### 친구 관리 및 채팅 기능
- 다른 사용자에게 친구 요청을 보내고, 받은 요청을 수락하거나 거절할 수 있습니다.
- 친구로 등록된 사용자와 실시간으로 채팅을 주고 받을 수 있습니다.
- 새로운 채팅이 온 채팅방이 실시간으로 업데이트 되며, 각 채팅방마다 안 읽은 메세지 수를 확인할 수 있습니다.

### 보드게임 관련 기능
- 키워드를 통해 찾고 싶은 보드게임을 검색하고, 장르나 인원수 필터를 적용해 더 구체적인 검색 결과를 얻을 수 있습니다.
- 특정 보드게임의 상세 정보를 조회하고, 그 보드게임에 대한 다른 친구의 평가를 확인할 수 있습니다.
- 특정 보드게임을 찜하거나 별점을 남길 수 있습니다.
- 찜하거나, 평가한 보드게임들을 조회할 수 있습니다.

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
