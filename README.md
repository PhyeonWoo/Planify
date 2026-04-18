# Planify - SaaS 프로젝트 관리 플랫폼

[![Java](https://img.shields.io/badge/Java-21%2B-orange)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Redis](https://img.shields.io/badge/Redis-7-red)](https://redis.io/)

팀 협업을 위한 **프로젝트 관리 SaaS 플랫폼** 입니다.
\
\
\
\
🎯 **프로젝트 개요**

Planify는 팀의 프로젝트와 할일을 효율적으로 관리해주는 프로젝트입니다.
다중 워크스페이스 지원, 실시간 알림, 권한 관리, 댓글 기능 등을 통해 팀 협업을 극대화합니다.

**주요 특징**:

✅ 다중 워크스페이스 지원\
✅ 프로젝트 & 할일 계층적 관리 (서브태스크)\
✅ 실시간 알림 시스템\
✅ 권한 기반 접근 제어 \
✅ JWT 기반 인증\
✅ 대댓글 지원 댓글 시스템\
✅ 할일 상태/우선순위 관리\
✅ 사용자 맞춤 알림 설정
\
\
\
**기술 스택**

**분류**	                   **기술**
Backend	         Spring Boot, Java 21
Security	     Spring Security, JWT, BCrypt
ORM	             MyBatis
Database	     MySQL
Cache	         Redis
Payment	         Toss Payments API
Docs	         SpringDoc OpenAPI (Swagger UI)
Test	         JUnit 5, Mockito
  

\
\

**핵심 구현 기능**

**1. 인증/인가 (JWT + Redis)**
- 회원가입 시 이메일 중복 검증, 비밀번호 정규식 검증 (영문+숫자+특수문자)
- Access Token (30분) / Refresh Token (7일) 이중 토큰 구조
- Redis를 이용한 Refresh Token 저장 및 토큰 갱신
- 로그아웃 시 Access Token을 Redis 블랙리스트에 등록하여 재사용 차단

**2. 워크스페이스 / 프로젝트 관리**
- 워크스페이스 CRUD 및 멤버 초대/역할 관리
- 토큰 기반 초대 시스템 - 만료일, 사용 횟수 추적
- 프로젝트 CRUD 및 멤버별 역할 분리
- 전 엔티티 Soft Delete 적용 (deletedDt 타임스탬프)

**3. 태스크 관리**
- 태스크 CRUD + 우선순위, 상태, 마감일, 담당자 설정
- 부모-자식 관계를 통한 서브태스크 계층 구조
- sortOrder 필드로 드래그 앤 드롭 정렬 지원
- 태스크 댓글 시스템
  
**4. 결제 / 구독 (Toss Payments)**
- FREE / PRO / ENTERPRISE 플랜 관리 (멤버 수, 워크스페이스 수 한도 설정)
- 구독 상태 머신: PENDING → ACTIVE → CANCELLED
- Toss Payments API 연동 결제 검증 (개발 환경 Mock 처리 포함)
- 결제 이력, 청구서 자동 생성 및 조회
- 구독 취소 시 환불 처리
- 플랜 한도 검증 - 워크스페이스/멤버 생성 시 구독 플랜 한도 체크
  
**5. 알림 시스템**
- 태스크, 댓글, 초대 등 유형별 알림
- 읽음/안읽음 상태 관리, 일괄 읽음 처리
- 사용자별 알림 설정


**아키텍처 설계 포인트**

**계층 구조**
Controller → Service (Interface/Impl) → Mapper (MyBatis) → DB


**API 구조 요약**

   **도메인**	        **엔드포인트**	                    **주요 기능**
Auth	        /api/v1/auth/**	            회원가입, 로그인, 로그아웃, 토큰 재발급
Workspace	    /api/v1/workspace/**	    워크스페이스 CRUD, 멤버 관리, 초대
Project	        /api/v1/project/**	        프로젝트 CRUD, 멤버 관리
Task	        /api/v1/task/**	            태스크 CRUD, 댓글, 상태/순서 변경
Notification	/api/v1/noti/**	            알림 조회, 읽음 처리, 설정
Payment	        /api/v1/payment/**	        플랜, 구독, 결제, 청구서




