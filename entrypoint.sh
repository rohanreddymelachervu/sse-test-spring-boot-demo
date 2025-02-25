#!/bin/sh
# Use the REDIS_PASSWORD environment variable
exec java -Dspring.redis.password="$REDIS_PASSWORD" -jar app.jar