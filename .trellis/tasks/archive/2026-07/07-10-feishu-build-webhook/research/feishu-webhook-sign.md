# 飞书自定义机器人 WebHook 签名与卡片

## 签名算法（官方）

开启签名校验后，请求体需包含 `timestamp`（秒）与 `sign`。

算法：将 `timestamp + "\n" + secret` 作为 HMAC **key**，对**空消息**做 HmacSHA256，再 Base64 编码。

Bash 参考：

```bash
timestamp=$(date +%s)
string_to_sign=$(printf '%s\n%s' "${timestamp}" "${WEBHOOK_SECRET}")
sign=$(printf '' | openssl dgst -sha256 -hmac "${string_to_sign}" -binary | openssl base64 -A)
```

时间戳须在当前时间 1 小时内。

## 卡片消息

```json
{
  "timestamp": "1599360473",
  "sign": "...",
  "msg_type": "interactive",
  "card": {
    "header": { "title": { "tag": "plain_text", "content": "..." }, "template": "blue" },
    "elements": [
      { "tag": "div", "text": { "tag": "lark_md", "content": "..." } },
      { "tag": "action", "actions": [ { "tag": "button", "text": { "tag": "plain_text", "content": "工作流" }, "url": "...", "type": "primary" } ] }
    ]
  }
}
```

自定义机器人卡片不支持发送后更新；本任务采用多条卡片方案。

## 参考

- https://open.feishu.cn/document/ukTMukTMukTM/ucTM5YjL3ETO24yNxkjN
- https://open.feishu.cn/document/common-capabilities/message-card/getting-started/send-message-cards-with-a-custom-bot
