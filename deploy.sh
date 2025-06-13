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

# 환경 변수 등록
export SPRING_DATASOURCE_URL="$SPRING_DATASOURCE_URL"
export SPRING_DATASOURCE_USERNAME="$SPRING_DATASOURCE_USERNAME"
export SPRING_DATASOURCE_PASSWORD="$SPRING_DATASOURCE_PASSWORD"
export MAIL_USERNAME="$MAIL_USERNAME"
export MAIL_PASSWORD="$MAIL_PASSWORD"
export JWT_SECRET_KEY="$JWT_SECRET_KEY"
export COOLSMS_API_KEY="$COOLSMS_API_KEY"
export COOLSMS_API_SECRET="$COOLSMS_API_SECRET"
export COOLSMS_API_NUMBER="$COOLSMS_API_NUMBER"

# 백그라운드로 애플리케이션 실행
echo "Starting application..."
nohup java \
  -jar $JAR_PATH --spring.profiles.active=prod > $LOG_PATH 2>&1 &
