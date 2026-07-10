#!/usr/bin/env bash
# 飞书自定义机器人 WebHook 卡片通知（openssl 签名 + curl）
# Secrets（由调用方注入，勿硬编码）:
#   FEISHU_BUILD_WEBHOOK_URL / FEISHU_BUILD_WEBHOOK_SECRET  — 打包
#   FEISHU_CI_WEBHOOK_URL    / FEISHU_CI_WEBHOOK_SECRET     — CI 检查
#
# 环境变量:
#   WEBHOOK_URL, WEBHOOK_SECRET
#   STATUS: start|success|failure
#   SCENE: build|check
#   BUILD_ID, APP_NAME, BUILD_TYPE_LABEL, TRIGGER_LABEL, BRANCH
#   VERSION_NAME, VERSION_CODE, GIT_SHA, GIT_MESSAGE
#   RUN_URL, JOB_URL
#     - SCENE=check：各状态均尽量带「工作流」+「Gradle 日志」；JOB_URL 空则回退 RUN_URL
#     - SCENE=build：开始仅「工作流」；成功/失败再加「Gradle 日志」
#   CREATED_AT, UPDATED_AT
#
# 任何异常均 exit 0，避免拖垮 CI/打包。
set -uo pipefail
trap 'echo "::warning::飞书通知脚本异常(line=${LINENO})，已忽略"; exit 0' ERR

if [ -z "${WEBHOOK_URL:-}" ]; then
  echo "::warning::WEBHOOK_URL 为空，跳过飞书通知"
  exit 0
fi

if [ -z "${WEBHOOK_SECRET:-}" ]; then
  echo "::warning::WEBHOOK_SECRET 为空，跳过飞书通知"
  exit 0
fi

STATUS="${STATUS:-}"
SCENE="${SCENE:-build}"
BUILD_ID="${BUILD_ID:-}"
APP_NAME="${APP_NAME:-}"
BUILD_TYPE_LABEL="${BUILD_TYPE_LABEL:-}"
TRIGGER_LABEL="${TRIGGER_LABEL:-}"
BRANCH="${BRANCH:-}"
VERSION_NAME="${VERSION_NAME:-}"
VERSION_CODE="${VERSION_CODE:-}"
GIT_SHA="${GIT_SHA:-}"
GIT_MESSAGE="${GIT_MESSAGE:-}"
RUN_URL="${RUN_URL:-}"
JOB_URL="${JOB_URL:-}"
CREATED_AT="${CREATED_AT:-}"
UPDATED_AT="${UPDATED_AT:-}"

# 取 git 描述首行，并去掉易破坏 Markdown/JSON 的控制字符
GIT_MESSAGE_LINE=$(printf '%s' "${GIT_MESSAGE}" | head -n 1 | tr -d '\r' | tr '\n\t' '  ')

case "${SCENE}" in
  check)
    case "${STATUS}" in
      start)   TITLE="检查开始🚀"; TEMPLATE="blue" ;;
      success) TITLE="检查成功✅"; TEMPLATE="green" ;;
      failure) TITLE="检查失败❌"; TEMPLATE="red" ;;
      *)
        echo "::warning::未知 STATUS=${STATUS}，跳过飞书通知"
        exit 0
        ;;
    esac
    ;;
  build|*)
    case "${STATUS}" in
      start)   TITLE="打包开始🚀"; TEMPLATE="blue" ;;
      success) TITLE="打包成功✅"; TEMPLATE="green" ;;
      failure) TITLE="打包失败❌"; TEMPLATE="red" ;;
      *)
        echo "::warning::未知 STATUS=${STATUS}，跳过飞书通知"
        exit 0
        ;;
    esac
    ;;
esac

STATUS_TEXT="${TITLE}"
# 卡片 header：打包 id + 状态文案，例如「123 - 检查开始🚀」
HEADER_TITLE="${BUILD_ID} - ${TITLE}"

MD_CONTENT=$(cat <<EOF
**打包 id：** ${BUILD_ID}
**状态：** ${STATUS_TEXT}
**应用：** ${APP_NAME}/${BUILD_TYPE_LABEL}
**触发方式：** ${TRIGGER_LABEL}
**分支：** ${BRANCH}
**版本：** ${VERSION_NAME}（${VERSION_CODE}）
**Git：** ${GIT_SHA}
**提交：** ${GIT_MESSAGE_LINE}
**创建时间：** ${CREATED_AT}
**更新时间：** ${UPDATED_AT}
EOF
)

# 飞书签名：timestamp + "\n" + secret 作为 HMAC key，对空消息做 HmacSHA256 再 Base64
timestamp=$(date +%s)
string_to_sign=$(printf '%s\n%s' "${timestamp}" "${WEBHOOK_SECRET:-}")
sign=$(printf '' | openssl dgst -sha256 -hmac "${string_to_sign}" -binary | openssl base64 -A)

# CI 检查：各状态均展示「工作流」+「Gradle 日志」；打包开始仅「工作流」
NEED_GRADLE_BTN=0
if [ "${SCENE}" = "check" ]; then
  NEED_GRADLE_BTN=1
elif [ "${STATUS}" != "start" ]; then
  NEED_GRADLE_BTN=1
fi

if [ "${NEED_GRADLE_BTN}" -eq 1 ]; then
  EFFECTIVE_JOB_URL="${JOB_URL}"
  if [ -z "${EFFECTIVE_JOB_URL}" ]; then
    if [ "${SCENE}" = "check" ] && [ -n "${RUN_URL}" ]; then
      EFFECTIVE_JOB_URL="${RUN_URL}"
      echo "::warning::JOB_URL 为空（SCENE=check STATUS=${STATUS}），Gradle 日志回退为 RUN_URL"
    else
      echo "::warning::JOB_URL 为空（STATUS=${STATUS}），仍尝试推送（仅工作流按钮）"
    fi
  fi
  if [ -n "${EFFECTIVE_JOB_URL}" ]; then
    ACTIONS_JSON=$(jq -n \
      --arg run_url "${RUN_URL}" \
      --arg job_url "${EFFECTIVE_JOB_URL}" \
      '[
        {
          tag: "button",
          text: { tag: "plain_text", content: "工作流" },
          url: $run_url,
          type: "primary"
        },
        {
          tag: "button",
          text: { tag: "plain_text", content: "Gradle 日志" },
          url: $job_url,
          type: "default"
        }
      ]')
  else
    ACTIONS_JSON=$(jq -n \
      --arg run_url "${RUN_URL}" \
      '[{
        tag: "button",
        text: { tag: "plain_text", content: "工作流" },
        url: $run_url,
        type: "primary"
      }]')
  fi
else
  # 打包开始：仅工作流
  ACTIONS_JSON=$(jq -n \
    --arg run_url "${RUN_URL}" \
    '[{
      tag: "button",
      text: { tag: "plain_text", content: "工作流" },
      url: $run_url,
      type: "primary"
    }]')
fi

PAYLOAD=$(jq -n \
  --arg timestamp "${timestamp}" \
  --arg sign "${sign}" \
  --arg title "${HEADER_TITLE}" \
  --arg template "${TEMPLATE}" \
  --arg md "${MD_CONTENT}" \
  --argjson actions "${ACTIONS_JSON}" \
  '{
    timestamp: $timestamp,
    sign: $sign,
    msg_type: "interactive",
    card: {
      header: {
        title: { tag: "plain_text", content: $title },
        template: $template
      },
      elements: [
        { tag: "div", text: { tag: "lark_md", content: $md } },
        { tag: "action", actions: $actions }
      ]
    }
  }')

echo "推送飞书卡片: SCENE=${SCENE} STATUS=${STATUS} TITLE=${HEADER_TITLE}"
HTTP_CODE=0
RESPONSE=""
set +e
RESPONSE=$(curl -sS -w "\n%{http_code}" -X POST \
  -H "Content-Type: application/json" \
  -d "${PAYLOAD}" \
  "${WEBHOOK_URL}")
CURL_EXIT=$?
set -e

if [ "${CURL_EXIT}" -ne 0 ]; then
  echo "::warning::飞书通知 curl 失败 (exit=${CURL_EXIT})，不影响构建"
  exit 0
fi

HTTP_CODE=$(printf '%s' "${RESPONSE}" | tail -n 1)
BODY=$(printf '%s' "${RESPONSE}" | sed '$d')

if [ "${HTTP_CODE}" != "200" ]; then
  echo "::warning::飞书通知 HTTP ${HTTP_CODE}，响应: ${BODY}"
  exit 0
fi

# 飞书业务错误码也打印但不失败构建
CODE=$(printf '%s' "${BODY}" | jq -r '.code // .StatusCode // empty' 2>/dev/null || true)
if [ -n "${CODE}" ] && [ "${CODE}" != "0" ]; then
  echo "::warning::飞书通知业务失败，响应: ${BODY}"
  exit 0
fi

echo "飞书通知成功: ${BODY}"
exit 0
