#!/bin/bash
set -euo pipefail

NACOS_ADDR="localhost:8848"
NAMESPACE="prod"
REQUIRED_SERVICES=("ruoyi-auth" "ruoyi-system" "hjm-music-web")

# Login to get token (optional)
echo "🔍 Checking service registry in namespace '${NAMESPACE}'..."
LOGIN_CMD="curl -s -X POST 'http://${NACOS_ADDR}/nacos/v1/auth/users/login' -d 'username=nacos&password=nacos'"
RESPONSE=$(eval "$LOGIN_CMD" || echo "")
TOKEN=$(echo "$RESPONSE" | sed -E 's/.*"accessToken":"([^"]*)".*/\1/' || echo "")

if [ -z "$TOKEN" ] || [ "$TOKEN" == "$RESPONSE" ]; then
    TOKEN_PARAM=""
else
    TOKEN_PARAM="&accessToken=${TOKEN}"
fi

# Fetch service list
LIST_URL="http://${NACOS_ADDR}/nacos/v1/ns/service/list?pageNo=1&pageSize=100&namespaceId=${NAMESPACE}${TOKEN_PARAM}"
SERVICE_JSON=$(curl --fail -s "${LIST_URL}")

echo "Registered services: ${SERVICE_JSON}"

MISSING=0
for svc in "${REQUIRED_SERVICES[@]}"; do
    if echo "${SERVICE_JSON}" | grep -q "\"${svc}\""; then
        echo "✅ Service '${svc}' is registered."
    else
        echo "❌ Service '${svc}' is MISSING!"
        MISSING=$((MISSING + 1))
    fi

    INSTANCE_URL="http://${NACOS_ADDR}/nacos/v1/ns/instance/list?serviceName=${svc}&namespaceId=${NAMESPACE}${TOKEN_PARAM}"
    INSTANCE_JSON=$(curl --fail -s "${INSTANCE_URL}")
    if echo "${INSTANCE_JSON}" | grep -Eq '"healthy"[[:space:]]*:[[:space:]]*true'; then
        echo "✅ Service '${svc}' has at least one healthy instance."
    else
        echo "❌ Service '${svc}' has no healthy instances: ${INSTANCE_JSON}"
        MISSING=$((MISSING + 1))
    fi
done

if [ $MISSING -gt 0 ]; then
    echo "🚨 Error: $MISSING required services are not registered in Nacos."
    exit 1
fi

echo "🎉 All required services are successfully registered."
exit 0
