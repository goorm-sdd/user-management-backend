#!/bin/bash

APP_NAME=user-management-backend
JAR_NAME=user-management-backend-0.0.1-SNAPSHOT.jar
JAR_PATH=./build/libs/$JAR_NAME
LOG_PATH=app.log

# 1. 실행 중인 프로세스 종료
PID=$(pgrep -f $JAR_NAME)
if [ -n "$PID" ]; then
  echo "Stopping running process: $PID"
  kill -15 "$PID"
  sleep 5
fi

# 2. JAR 파일 존재 여부 확인
if [ ! -f "$JAR_PATH" ]; then
  echo "❌ JAR 파일이 존재하지 않습니다: $JAR_PATH"
  echo "CI/CD에서 JAR 복사 확인 필요"
  exit 1
fi

# 3. 애플리케이션 실행
echo "Starting application..."
nohup java \
  -Duser.timezone=Asia/Seoul \
  -Dspring.datasource.url="$SPRING_DATASOURCE_URL" \
  -Dspring.datasource.username="$SPRING_DATASOURCE_USERNAME" \
  -Dspring.datasource.password="$SPRING_DATASOURCE_PASSWORD" \
  -Dspring.mail.username="$MAIL_USERNAME" \
  -Dspring.mail.password="$MAIL_PASSWORD" \
  -Djwt.secret-key="$JWT_SECRET_KEY" \
  -Dcoolsms.api.key="$COOLSMS_API_KEY" \
  -Dcoolsms.api.secret="$COOLSMS_API_SECRET" \
  -Dcoolsms.api.number="$COOLSMS_API_NUMBER" \
  -jar "$JAR_PATH" --spring.profiles.active=prod > "$LOG_PATH" 2>&1 &

echo "✅ 배포 스크립트 완료. 로그 확인: tail -f $LOG_PATH"
