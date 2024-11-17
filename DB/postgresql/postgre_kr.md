Docker로 postgre 설치 시 
docker image 한국어가 깨짐 
아래와 같은 image 셋팅으로 한글이 가능 하게 변경

이미지 기준으로는 16이지만 14까지 바꾸어 테스트 해보니 모두 다 동일하게 한글이 잘 나오는걸 테스트함

아래에 주석 처리 된 부분을 수정하면 처음 실행 시 실행 시킬 쉘과 sql문을 삽입 가능


```bash
FROM postgres:16

# Locale 설정에 필요한 패키지 설치
RUN apt-get update && apt-get install -y locales openssh-client

# ko_KR.UTF-8 locale 생성
RUN locale-gen ko_KR.UTF-8

# ko_KR.UTF-8 locale 생성
RUN localedef -i ko_KR -f UTF-8 ko_KR.UTF-8

# 환경 변수 설정
ENV LANG=ko_KR.UTF-8
ENV LC_ALL=ko_KR.UTF-8
ENV TZ Asia/Seoul

# init-user-db.sh 스크립트 복사
#COPY backupdb.sh /opt/
# COPY init-database.sh /docker-entrypoint-initdb.d/

# 스크립트에 실행 권한 부여
# RUN chmod +x /docker-entrypoint-initdb.d/init-database.sh
# RUN chown postgres:postgres /docker-entrypoint-initdb.d/init-database.sh
# RUN chmod +x /opt/backupdb.sh
# RUN cjpwm postgres:postgres /opt/backupdb.sh

# PostgreSQL 데이터 디렉토리 초기화
# RUN mkdir -p /var/lib/postgresql/data
# RUN chown -R postgres:postgres /var/lib/postgresql

# gosu 설치
# RUN apt-get install -y gosu

# initdb 명령을 postgres 사용자로 실행
# RUN gosu postgres /usr/lib/postgresql/16/bin/initdb -D /var/lib/postgresql/data


VOLUME /var/lib/postgresql/data

EXPOSE 5432

CMD ["postgres"]
```
