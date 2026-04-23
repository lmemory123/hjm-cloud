#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MAVEN4_BIN="${MAVEN4_BIN:-/Users/momao/.cache/apache-maven-4.0.0-rc-4/bin/mvn}"

cd "${ROOT_DIR}"

"${MAVEN4_BIN}" -B -ntp -pl ruoyi-gateway -am install -DskipTests

exec /Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home/bin/java \
  -Dspring.output.ansi.enabled=always \
  -jar "${ROOT_DIR}/ruoyi-gateway/target/ruoyi-gateway.jar"
