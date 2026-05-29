import http from 'k6/http';
import { check, sleep } from 'k6';

/**
 * 哈基哈米后端性能压测脚本 (k6)
 * 
 * 运行方式:
 * 1. 安装 k6: brew install k6 (macOS) 或 https://k6.io/docs/getting-started/installation/
 * 2. 运行脚本: k6 run stress-test.js
 */

export const options = {
  // 定义阶段负载
  stages: [
    { duration: '1m', target: 50 },  // 1分钟内升压到 50 个并发用户
    { duration: '3m', target: 50 },  // 保持 50 个并发 3 分钟
    { duration: '1m', target: 100 }, // 1分钟内升压到 100
    { duration: '3m', target: 100 }, // 保持 100
    { duration: '1m', target: 0 },   // 降压到 0
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% 的请求应在 500ms 内完成
    http_req_failed: ['rate<0.01'],    // 失败率应低于 1%
  },
};

const BASE_URL = __ENV.API_BASE_URL || 'http://localhost:8080/music/open/api/v1';
const API_KEY = __ENV.API_KEY || 'dev_your_token_here';

export default function () {
  const params = {
    headers: {
      'X-Hakimi-Api-Key': API_KEY,
      'Content-Type': 'application/json',
    },
  };

  // 1. 测试元信息与统计
  const resMeta = http.get(`${BASE_URL}/meta`, params);
  check(resMeta, {
    'meta status is 200': (r) => r.status === 200,
  });

  sleep(1);

  // 2. 测试歌曲搜索列表
  const resSongs = http.get(`${BASE_URL}/songs?keyword=测试&pageSize=10`, params);
  check(resSongs, {
    'songs status is 200': (r) => r.status === 200,
    'songs has data': (r) => JSON.parse(r.body).rows !== undefined,
  });

  sleep(1);

  // 3. 测试高频详情页 (假设 ID 为 1001)
  const resDetail = http.get(`${BASE_URL}/songs/1001`, params);
  check(resDetail, {
    'detail status is 200 or 404': (r) => r.status === 200 || r.status === 404,
  });

  sleep(1);

  // 4. 测试统计概览
  const resStats = http.get(`${BASE_URL}/stats/overview`, params);
  check(resStats, {
    'stats status is 200': (r) => r.status === 200,
  });

  sleep(2);
}
