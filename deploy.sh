#!/bin/bash

echo "[INFO] 🚀 Deploy script started."

# ========================
# 1. 환경 변수 export
# ========================
export SPRING_DATASOURCE_URL=$SPRING_DATASOURCE_URL
export SPRING_DATASOURCE_USERNAME=$SPRING_DATASOURCE_USERNAME
export SPRING_DATASOURCE_PASSWORD=$SPRING_DATASOURCE_PASSWORD
export JWT_SECRET_KEY=$JWT_SECRET_KEY
export COOLSMS_API_KEY=$COOLSMS_API_KEY
export COOLSMS_API_SECRET=$COOLSMS_API_SECRET
export COOLSMS_API_NUMBER=$COOLSMS_API_NUMBER
export MAIL_USERNAME=$MAIL_USERNAME
export MAIL_PASSWORD=$MAIL_PASSWORD

# 확인용 출력 (민감 정보는 마스킹)
echo "[INFO] 📦 DB URL: $SPRING_DATASOURCE_URL"
echo "[INFO] 🔐 JWT KEY Length: ${#JWT_SECRET_KEY} characters"
echo "[INFO] 📧 MAIL USER: $MAIL_USERNAME"
echo "[INFO] 📱 COOLSMS KEY: $COOLSMS_API_KEY"

# ========================
# 2. 기존 프로세스 종료
# ========================
PID=$(pgrep -f 'user-management-backend-0.0.1-SNAPSHOT.jar')
if [ -n "$PID" ]; then
  echo "[INFO] 🔁 기존 프로세스 종료 중: PID $PID"
  kill -9 $PID
  sleep 1
fi

# ========================
# 3. 앱 실행
# ========================
echo "[INFO] 🟢 Spring Boot 앱 실행 시작..."
nohup java -jar build/libs/user-management-backend-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod > log.out 2>&1 &

echo "[INFO] ✅ 배포 완료! 로그 확인: tail -f /home/ec2-user/app/log.out"
