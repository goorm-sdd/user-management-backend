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

# 백그라운드로 애플리케이션 실행
echo "Starting application..."
nohup java \
  -DJWT_SECRET_KEY="$JWT_SECRET_KEY" \
  -DSPRING_DATASOURCE_URL="$SPRING_DATASOURCE_URL" \
  -DSPRING_DATASOURCE_USERNAME="$SPRING_DATASOURCE_USERNAME" \
  -DSPRING_DATASOURCE_PASSWORD="$SPRING_DATASOURCE_PASSWORD" \
  -DMAIL_USERNAME="$MAIL_USERNAME" \
  -DMAIL_PASSWORD="$MAIL_PASSWORD" \
  -DCOOLSMS_API_KEY="$COOLSMS_API_KEY" \
  -DCOOLSMS_API_SECRET="$COOLSMS_API_SECRET" \
  -DCOOLSMS_API_NUMBER="$COOLSMS_API_NUMBER" \
  -jar $JAR_PATH --spring.profiles.active=prod > $LOG_PATH 2>&1 &