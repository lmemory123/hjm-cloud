/**
 * 哈基哈米 Webhook 验证接收端演示脚本 (Node.js)
 * 
 * 用途：验证后端推送的 Webhook 签名 (HMAC-SHA256) 及 Payload 完整性。
 * 
 * 使用方式：
 * 1. 安装依赖：npm install express crypto
 * 2. 运行脚本：node webhook-receiver-demo.js
 * 3. 在 hjm-admin 后台配置 Webhook 地址为：http://<your-ip>:3000/webhook
 * 4. 设置签名密钥 (Secret) 为：hjm_test_secret_123
 */

const express = require('express');
const crypto = require('crypto');

const app = express();
const port = 3000;
const WEBHOOK_SECRET = 'hjm_test_secret_123'; // 与后台配置保持一致

app.use(express.json());

app.post('/webhook', (req, res) => {
    const signature = req.headers['x-hakimi-signature'];
    const timestamp = req.headers['x-hakimi-timestamp'];
    const eventType = req.headers['x-hakimi-event'];
    
    console.log(`\n[${new Date().toISOString()}] 收到 Webhook 事件: ${eventType}`);
    
    if (!signature || !timestamp) {
        console.error('❌ 验证失败：缺失签名或时间戳请求头');
        return res.status(401).send('Missing signature/timestamp');
    }

    // 1. 验证时间戳（防重放，允许 5 分钟偏差）
    const now = Date.now();
    if (Math.abs(now - parseInt(timestamp)) > 5 * 60 * 1000) {
        console.error('❌ 验证失败：时间戳偏差过大');
        return res.status(401).send('Timestamp expired');
    }

    // 2. 验证签名
    const payload = JSON.stringify(req.body);
    const base = timestamp + '.' + payload;
    const expectedSignature = crypto
        .createHmac('sha256', WEBHOOK_SECRET)
        .update(base)
        .digest('base64');

    if (signature !== expectedSignature) {
        console.error('❌ 验证失败：签名不匹配');
        console.log('期望签名:', expectedSignature);
        console.log('实际签名:', signature);
        return res.status(401).send('Invalid signature');
    }

    console.log('✅ 验证成功！');
    console.log('数据内容:', JSON.stringify(req.body, null, 2));

    res.status(200).send('OK');
});

app.listen(port, () => {
    console.log(`🚀 Webhook 演示接收端已启动，监听端口: ${port}`);
    console.log(`请确保后端可访问到此地址，或使用 ngrok 等工具内网穿透。`);
});
