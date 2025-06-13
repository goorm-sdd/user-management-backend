#!/bin/bash

# 환경 변수 로드 (.env 파일이 존재하면)
if [ -f .env ]; then
  echo "📦 .env 파일을 로드합니다..."
  export $(grep -v '^#' .env | xargs)
else
  echo "⚠️ .env 파일이 존재하지 않습니다. 환경 변수가 로드되지 않을 수 있습니다."
fi

# 실행 중인 애플리케이션 종료 (PID로 찾기)
CURRENT_PID=$(pgrep -f 'user-management-backend-0.0.1-SNAPSHOT.jar')

if [ -n "$CURRENT_PID" ]; then
  echo "🛑 기존 애플리케이션 프로세스 종료: PID $CURRENT_PID"
  kill -15 $CURRENT_PID
  sleep 5
else
  echo "ℹ️ 실행 중인 애플리케이션이 없습니다."
fi

# 애플리케이션 실행
echo "🚀 새로운 애플리케이션을 실행합니다..."
nohup java -jar \
  -Dspring.profiles.active=prod \
  build/libs/user-management-backend-0.0.1-SNAPSHOT.jar > app.log 2>&1 &
