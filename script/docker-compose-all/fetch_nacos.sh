#!/bin/bash
set -e

if [ -z "$1" ]; then
    echo "Usage: $0 <dataId>"
    echo "Example: $0 application-common.yml"
    exit 1
fi

DATA_ID=$1
NACOS_ADDR="localhost:8848"
NAMESPACE="prod"
GROUP="DEFAULT_GROUP"

# Login to get token
RESPONSE=$(curl -s -X POST "http://${NACOS_ADDR}/nacos/v1/auth/users/login" -d "username=nacos&password=nacos")
TOKEN=$(echo "$RESPONSE" | sed -E 's/.*"accessToken":"([^"]*)".*/\1/')

if [ -z "$TOKEN" ] || [ "$TOKEN" == "$RESPONSE" ]; then
    echo "❌ Failed to login to Nacos."
    exit 1
fi

curl -s "http://${NACOS_ADDR}/nacos/v1/cs/configs?dataId=${DATA_ID}&group=${GROUP}&tenant=${NAMESPACE}&accessToken=${TOKEN}"
echo ""
