#!/bin/bash

APP_NAME=user-management-backend
JAR_PATH=build/libs/user-management-backend-0.0.1-SNAPSHOT.jar
LOG_PATH=app.log

# 기존 실행 중인 프로세스 종료
PID=$(pgrep -f $APP_NAME)
if [ -n "$PID" ]; then
  echo "Stopping running process: $PID"
  kill -15 $PID
  sleep 5
fi

# 애플리케이션 실행
echo "Starting application..."
nohup java \
  -Dspring.datasource.url="$SPRING_DATASOURCE_URL" \
  -Dspring.datasource.username="$SPRING_DATASOURCE_USERNAME" \
  -Dspring.datasource.password="$SPRING_DATASOURCE_PASSWORD" \
  -Dspring.mail.username="$MAIL_USERNAME" \
  -Dspring.mail.password="$MAIL_PASSWORD" \
  -Djwt.secret-key="$JWT_SECRET_KEY" \
  -Dcoolsms.api.key="$COOLSMS_API_KEY" \
  -Dcoolsms.api.secret="$COOLSMS_API_SECRET" \
  -Dcoolsms.api.number="$COOLSMS_API_NUMBER" \
  -jar $JAR_PATH --spring.profiles.active=prod > $LOG_PATH 2>&1 &
