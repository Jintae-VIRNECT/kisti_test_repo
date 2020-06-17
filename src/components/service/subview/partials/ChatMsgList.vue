<template>
  <vue2-scrollbar ref="chatListScrollbar">
    <ol class="chat-list">
      <li class="chat-item date">
        <p>{{ $dayjs().format('LL') }}</p>
      </li>
      <chat-item
        v-for="(chat, idx) of chatList"
        :key="idx"
        :beforeChat="idx === 0 ? null : chatList[idx - 1]"
        :afterChat="idx === chatList.length - 1 ? null : chatList[idx + 1]"
        :chat="chat"
      ></chat-item>
    </ol>
  </vue2-scrollbar>
</template>

<script>
import { mapGetters } from 'vuex'
import ChatItem from './ChatItem'
export default {
  name: 'ChatMsgList',
  components: {
    ChatItem,
  },
  computed: {
    ...mapGetters(['chatList']),
  },
  watch: {
    chatList: {
      handler() {
        this.$nextTick(() => {
          if (this.$refs['chatListScrollbar']) {
            this.$refs['chatListScrollbar'].scrollToY(Number.MAX_SAFE_INTEGER)
          }
        })
      },
      deep: true,
    },
  },
}
</script>

<style></style>
