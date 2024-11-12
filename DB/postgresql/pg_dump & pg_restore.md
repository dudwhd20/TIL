# PostgreSQL `pg_dump`와 `pg_restore`를 이용한 백업 및 복원 방법

`pg_dump`는 PostgreSQL 데이터베이스의 논리 백업 도구로, 데이터베이스 구조와 데이터를 SQL 형식으로 덤프하여 논리 백업할 수 있습니다. 이렇게 생성된 덤프 파일은 `pg_restore`를 사용하여 데이터베이스를 복원하는 데 활용됩니다.


## 1. 전체 데이터베이스 덤프 생성하기

전체 데이터베이스의 덤프 파일을 생성하는 기본 명령어는 다음과 같습니다.

```bash
pg_dump -U <사용자이름> -h <호스트명> -d <데이터베이스명> -F c -f <백업파일경로>
```

## 예시
pg_dump -U myuser -h localhost -d mydatabase -F c -f /path/to/backup/mydatabase_backup.dump


```bash
pg_restore -U <사용자이름> -d <데이터베이스명> -h <호스트명> -c -1 <백업파일경로>
```

## 예시
pg_restore -U myuser -d mydatabase -h localhost -c -1 /path/to/backup/mydatabase_backup.dump


## 출처

pg_dump: [pg_dump 공식문서](https://www.postgresql.org/docs/current/app-pgdump.html)

pg_restore: [pg_restore](https://www.postgresql.org/docs/current/app-pgrestore.html)
