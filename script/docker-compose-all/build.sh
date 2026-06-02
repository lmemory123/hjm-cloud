#!/bin/bash
set -e

# Hajihami Full-Stack Build Script

echo "🚀 Starting backend build (hjm-cloud)..."
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="${SCRIPT_DIR}/../.."

cd "${ROOT_DIR}"
mvn clean package -Dmaven.test.skip=true -pl ruoyi-gateway,ruoyi-auth,ruoyi-modules/ruoyi-system,ruoyi-modules/hjm-music-web,ruoyi-visual/ruoyi-nacos -am

echo "✅ Backend build completed!"
echo "💡 To start the system, run:"
echo "   cd script/docker-compose-all"
echo "   ./cold_start.sh"
