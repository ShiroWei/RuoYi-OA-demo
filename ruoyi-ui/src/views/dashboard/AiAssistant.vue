<template>
  <div class="ai-assistant">
    <!-- 悬浮球 -->
    <div v-if="!visible" class="ai-float" @click="open">
      <svg-icon icon-class="message" class-name="ai-float-icon" />
    </div>

    <!-- 对话面板 -->
    <transition name="el-zoom-in-bottom">
      <div v-if="visible" class="ai-panel">
        <div class="ai-header">
          <div class="ai-title">
            <svg-icon icon-class="message" />
            <span>智能助手</span>
          </div>
          <el-button icon="el-icon-close" circle size="mini" class="ai-close" @click="close" />
        </div>

        <div ref="body" class="ai-body">
          <div v-for="(m, i) in messages" :key="i" class="ai-msg" :class="m.role">
            <div class="ai-avatar" :class="m.role">{{ m.role === 'assistant' ? 'AI' : '我' }}</div>
            <div class="ai-bubble">
              <div class="ai-text">{{ m.reply }}</div>
              <template v-if="m.type === 'list' && m.items.length">
                <div v-for="(it, idx) in m.items" :key="idx" class="ai-item" @click="jump(it.jumpTo)">
                  <div class="ai-item-title">{{ it.title }}</div>
                  <div class="ai-item-desc">{{ it.desc }}</div>
                </div>
              </template>
              <template v-if="m.type === 'stat' && m.items.length">
                <div class="ai-stat-grid">
                  <div v-for="(it, idx) in m.items" :key="idx" class="ai-stat-item">
                    <div class="ai-stat-name">{{ it.name }}</div>
                    <div class="ai-stat-value">{{ it.value }}</div>
                  </div>
                </div>
              </template>
              <div v-if="m.action === 'jump' && m.jumpUrl" class="ai-link" @click="jump(m.jumpUrl)">前往查看 »</div>
            </div>
          </div>
          <div v-if="loading" class="ai-msg assistant">
            <div class="ai-avatar assistant">AI</div>
            <div class="ai-bubble ai-typing">思考中…</div>
          </div>
        </div>

        <div class="ai-footer">
          <el-input
            v-model="input"
            type="textarea"
            :rows="1"
            resize="none"
            placeholder="问我：我的待办 / 请假 / 今日日程 / 统计…"
            @keyup.enter.native.prevent="send"
          />
          <el-button type="primary" size="mini" class="ai-send" :loading="loading" @click="send">发送</el-button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { chatAi } from '@/api/dashboard'

export default {
  name: 'AiAssistant',
  data() {
    return {
      visible: false,
      loading: false,
      input: '',
      messages: [
        {
          role: 'assistant',
          reply: '您好，我是智能助手。可以问我：我的待办、请假/报销/出差进度、今日日程、同事人数、工作台统计等。',
          type: 'text',
          items: []
        }
      ]
    }
  },
  methods: {
    open() {
      this.visible = true
    },
    close() {
      this.visible = false
    },
    jump(path) {
      if (path) {
        this.$router.push(path)
        this.close()
      }
    },
    send() {
      const text = (this.input || '').trim()
      if (!text || this.loading) {
        return
      }
      this.messages.push({ role: 'user', reply: text, type: 'text', items: [] })
      this.input = ''
      this.loading = true
      this.scrollToBottom()
      chatAi(text).then(res => {
        const data = res.data || {}
        this.messages.push({
          role: 'assistant',
          reply: data.reply || '',
          type: data.type || 'text',
          items: data.items || [],
          action: data.action || '',
          jumpUrl: data.jumpUrl || ''
        })
        this.loading = false
        this.scrollToBottom()
      }).catch(() => {
        this.messages.push({ role: 'assistant', reply: '抱歉，服务暂时不可用，请稍后再试。', type: 'text', items: [] })
        this.loading = false
      })
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const el = this.$refs.body
        if (el) {
          el.scrollTop = el.scrollHeight
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.ai-float {
  position: fixed;
  right: 24px;
  bottom: 72px;
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1890ff, #36a3f7);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(24, 144, 255, 0.35);
  z-index: 2000;
  transition: transform 0.2s;

  &:hover {
    transform: scale(1.08);
  }

  .ai-float-icon {
    color: #fff;
    font-size: 24px;
  }
}

.ai-panel {
  position: fixed;
  right: 24px;
  bottom: 72px;
  width: 360px;
  height: 480px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.16);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  z-index: 2001;

  .ai-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 14px 16px;
    background: linear-gradient(135deg, #1890ff, #36a3f7);
    color: #fff;

    .ai-title {
      display: flex;
      align-items: center;
      font-weight: 600;
      font-size: 15px;

      .svg-icon {
        margin-right: 8px;
      }
    }

    .ai-close {
      background: rgba(255, 255, 255, 0.2);
      color: #fff;
      border: none;
    }
  }

  .ai-body {
    flex: 1;
    overflow-y: auto;
    padding: 12px;
    background: #f5f7fa;

    .ai-msg {
      display: flex;
      margin-bottom: 12px;

      &.user {
        flex-direction: row-reverse;

        .ai-bubble {
          background: #1890ff;
          color: #fff;
        }
      }

      .ai-avatar {
        width: 30px;
        height: 30px;
        border-radius: 50%;
        flex-shrink: 0;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        font-weight: 600;
        color: #fff;

        &.assistant {
          background: #1890ff;
        }

        &.user {
          background: #13ce66;
        }
      }

      .ai-bubble {
        max-width: 78%;
        margin: 0 8px;
        padding: 10px 12px;
        background: #fff;
        border-radius: 8px;
        font-size: 13px;
        line-height: 1.6;
        color: #333;
        word-break: break-word;
        white-space: pre-wrap;

        &.ai-typing {
          color: #909399;
        }

        .ai-text {
          white-space: pre-wrap;
        }

        .ai-item {
          margin-top: 8px;
          padding: 8px 10px;
          background: #f5f7fa;
          border-radius: 6px;
          cursor: pointer;
          transition: background 0.2s;

          &:hover {
            background: #e8f4ff;
          }

          .ai-item-title {
            font-weight: 600;
            color: #333;
          }

          .ai-item-desc {
            margin-top: 4px;
            font-size: 12px;
            color: #909399;
          }
        }

        .ai-stat-grid {
          display: flex;
          flex-wrap: wrap;
          margin-top: 8px;

          .ai-stat-item {
            width: 48%;
            margin: 1%;
            padding: 8px;
            background: #f5f7fa;
            border-radius: 6px;
            text-align: center;

            .ai-stat-name {
              font-size: 12px;
              color: #909399;
            }

            .ai-stat-value {
              margin-top: 4px;
              font-size: 14px;
              font-weight: 600;
              color: #1890ff;
            }
          }
        }

        .ai-link {
          margin-top: 8px;
          font-size: 12px;
          color: #1890ff;
          cursor: pointer;
        }
      }
    }
  }

  .ai-footer {
    display: flex;
    align-items: flex-end;
    padding: 10px;
    border-top: 1px solid #ebeef5;

    .el-textarea {
      flex: 1;

      ::v-deep .el-textarea__inner {
        border: none;
        box-shadow: none;
        background: #f5f7fa;
        border-radius: 6px;
      }
    }

    .ai-send {
      margin-left: 8px;
    }
  }
}
</style>

