#!/bin/bash

JAR_PATH="/home/ec2-user/app/build/libs/user-management-backend-0.0.1-SNAPSHOT.jar"
LOG_PATH="/home/ec2-user/app/deploy.log"
PID=$(pgrep -f $JAR_PATH)

if [ -n "$PID" ]; then
  echo ">>> kill -9 $PID"
  kill -9 $PID
  sleep 2
fi

echo ">>> deploying new jar..."
nohup java -jar $JAR_PATH > $LOG_PATH 2>&1 &
