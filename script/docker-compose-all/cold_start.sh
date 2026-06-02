#!/bin/bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "${SCRIPT_DIR}"

COMPOSE=(docker compose -f "${SCRIPT_DIR}/docker-compose.yml")
WAIT_TIMEOUT="${WAIT_TIMEOUT:-240}"

compose_up() {
    if [ "${SKIP_BUILD:-0}" = "1" ]; then
        "${COMPOSE[@]}" up -d "$@"
        return
    fi

    "${COMPOSE[@]}" up -d --build "$@"
}

wait_for_healthy() {
    local container="$1"
    local elapsed=0
    local status=""

    echo "Waiting for ${container} to become healthy..."
    while [ $elapsed -lt "$WAIT_TIMEOUT" ]; do
        status=$(docker inspect --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' "$container" 2>/dev/null || true)
        if [ "$status" = "healthy" ]; then
            echo "✅ ${container} is healthy."
            return 0
        fi
        sleep 5
        elapsed=$((elapsed + 5))
    done

    echo "❌ ${container} did not become healthy within ${WAIT_TIMEOUT}s. Current status: ${status:-missing}"
    "${COMPOSE[@]}" logs --tail 120 "$container" || true
    return 1
}

echo "Stopping existing Hajihami containers..."
"${COMPOSE[@]}" down --remove-orphans

if [ "${RESET_DATA:-0}" = "1" ]; then
    echo "RESET_DATA=1: removing local PostgreSQL data for a clean rehearsal..."
    rm -rf "${SCRIPT_DIR}/middleware/postgres/data"
fi

echo "Starting middleware and Nacos..."
compose_up hjm-postgres hjm-redis hjm-rabbitmq hjm-nacos
wait_for_healthy hjm-postgres
wait_for_healthy hjm-redis
wait_for_healthy hjm-rabbitmq
wait_for_healthy hjm-nacos

echo "Synchronizing Nacos configuration before backend startup..."
"${SCRIPT_DIR}/push_nacos_all.sh"

echo "Starting gateway and backend services..."
compose_up ruoyi-gateway ruoyi-auth ruoyi-system hjm-music-web
wait_for_healthy ruoyi-gateway
wait_for_healthy ruoyi-auth
wait_for_healthy ruoyi-system
wait_for_healthy hjm-music-web

echo "Starting admin and Nuxt frontend..."
compose_up hjm-admin hjm-frontend
wait_for_healthy hjm-admin
wait_for_healthy hjm-frontend

"${SCRIPT_DIR}/check_nacos_services.sh"
"${COMPOSE[@]}" ps

echo "✅ Hajihami cold start completed."
