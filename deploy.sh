#!/bin/bash

echo "[INFO] ✅ Deploy script started."

# 주요 환경 변수 확인
echo "📦 SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL"
echo "👤 SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME"
echo "🔐 SPRING_DATASOURCE_PASSWORD=********"
echo "🔑 JWT_SECRET_KEY=${#JWT_SECRET_KEY} characters"
echo "📧 MAIL_USERNAME=$MAIL_USERNAME"
echo "📱 COOLSMS_API_KEY=$COOLSMS_API_KEY"

# 기존 애플리케이션 종료
PID=$(pgrep -f 'user-management-backend-0.0.1-SNAPSHOT.jar')
if [ -n "$PID" ]; then
  echo "[INFO] 🔁 기존 프로세스 종료 중: PID $PID"
  kill -9 $PID
  sleep 1
fi

# 애플리케이션 실행
echo "[INFO] 🚀 Spring Boot 앱 실행 중..."
nohup java -jar build/libs/user-management-backend-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod > log.out 2>&1 &

echo "[INFO] ✅ 배포 완료! 로그 확인: tail -f log.out"
