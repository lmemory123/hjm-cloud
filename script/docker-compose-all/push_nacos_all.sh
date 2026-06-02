#!/bin/bash
set -euo pipefail

# Configuration
NACOS_ADDR="localhost:8848"
NAMESPACE="prod"
GROUP="DEFAULT_GROUP"
MAX_RETRIES=5
RETRY_DELAY=3
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONFIG_DIR="${SCRIPT_DIR}/../config/nacos"

echo "🚀 Starting robust Nacos configuration push to '${NAMESPACE}'..."

# Function to run curl with retries
run_curl_with_retry() {
    local cmd="$1"
    local description="$2"
    local attempt=1
    while [ $attempt -le $MAX_RETRIES ]; do
        if eval "$cmd"; then
            return 0
        fi
        echo "⚠️  Attempt $attempt failed for: $description. Retrying in ${RETRY_DELAY}s..."
        sleep $RETRY_DELAY
        attempt=$((attempt + 1))
    done
    echo "❌ Failed after $MAX_RETRIES attempts: $description"
    return 1
}

# Publish one config and require Nacos to acknowledge it with "true".
push_config_with_retry() {
    local url="$1"
    local file="$2"
    local description="$3"
    local attempt=1
    local result=""
    while [ $attempt -le $MAX_RETRIES ]; do
        if result=$(curl --fail --silent --show-error -X POST "${url}" --data-urlencode "content@${file}") && [ "${result}" = "true" ]; then
            return 0
        fi
        echo "⚠️  Attempt $attempt failed for: $description. Nacos response: ${result:-<empty>}. Retrying in ${RETRY_DELAY}s..."
        sleep $RETRY_DELAY
        attempt=$((attempt + 1))
    done
    echo "❌ Failed after $MAX_RETRIES attempts: $description"
    return 1
}

# Wait for Nacos to be ready
echo "Waiting for Nacos to be healthy..."
run_curl_with_retry "curl --fail --silent --show-error --output /dev/null http://${NACOS_ADDR}/nacos/index.html" "Nacos Health Check"

# Login (optional if auth disabled)
echo "Attempting login..."
LOGIN_CMD="curl -s -X POST 'http://${NACOS_ADDR}/nacos/v1/auth/users/login' -d 'username=nacos&password=nacos'"
RESPONSE=$(eval "$LOGIN_CMD" || echo "")
TOKEN=$(echo "$RESPONSE" | sed -E 's/.*"accessToken":"([^"]*)".*/\1/' || echo "")

if [ -z "$TOKEN" ] || [ "$TOKEN" == "$RESPONSE" ]; then
    echo "⚠️  Nacos login failed or not required. Proceeding without token."
    TOKEN_PARAM=""
else
    echo "✅ Logged into Nacos successfully."
    TOKEN_PARAM="&accessToken=${TOKEN}"
fi

# Ensure namespace exists
echo "Ensuring namespace '${NAMESPACE}' exists..."
NS_URL="http://${NACOS_ADDR}/nacos/v1/console/namespaces"
run_curl_with_retry "curl --fail --silent --show-error --output /dev/null -X POST '${NS_URL}' -d 'customNamespaceId=${NAMESPACE}&namespaceName=${NAMESPACE}${TOKEN_PARAM}'" "Create Namespace"

# Push configs
configs=(
    "application-common.yml"
    "datasource.yml"
    "ruoyi-gateway.yml"
    "ruoyi-auth.yml"
    "ruoyi-system.yml"
    "hjm-music-web.yml"
)

for config in "${configs[@]}"; do
    file="${CONFIG_DIR}/${config}"
    if [ ! -f "$file" ]; then
        echo "❌ Error: Required config file $file not found!"
        exit 1
    fi

    echo "Pushing $config to namespace '${NAMESPACE}'..."
    URL="http://${NACOS_ADDR}/nacos/v1/cs/configs?dataId=${config}&group=${GROUP}&tenant=${NAMESPACE}&type=yaml${TOKEN_PARAM}"
    push_config_with_retry "${URL}" "${file}" "Push ${config} to namespace '${NAMESPACE}'"

    echo "Pushing $config to public namespace (fallback)..."
    URL_PUBLIC="http://${NACOS_ADDR}/nacos/v1/cs/configs?dataId=${config}&group=${GROUP}&type=yaml${TOKEN_PARAM}"
    push_config_with_retry "${URL_PUBLIC}" "${file}" "Push ${config} to public namespace"

    echo "✅ Successfully pushed $config"
done

echo "🎉 All configurations processed successfully with verification."
