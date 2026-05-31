#!/bin/bash

# 哈基哈米一键构建脚本

echo "🚀 开始构建后端项目 (hjm-cloud)..."
# 回到项目根目录
cd ../../
mvn clean package -DskipTests

# 复制 hjm-music-web.jar 到 docker 目录
echo "📦 准备后端镜像资源..."
cp ruoyi-modules/hjm-music-web/target/hjm-music-web.jar script/docker-compose-all/backend/

echo "✅ 构建完成！"
echo "💡 请运行: cd script/docker-compose-all && docker-compose up --build -d"
