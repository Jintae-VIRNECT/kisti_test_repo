<template>
	<div class="chat-input">
    <button class="chat-input__upload">파일 업로드</button>
    <input 
        class="chat-input__write" 
        v-model="inputText" 
        placeholder="메시지를 입력하세요."
        @keydown.enter="doSend()">
    <button class="chat-input__button" @click="doSend()">보내기</button>
  </div>
</template>

<script>
export default {
	name: "ChatInput",
	components: {},
	data() {
		return {
      inputText: ''
    }
  },
  props: {
    chat: Object
  },
	computed: {},
	watch: {},
	methods: {
    doSend() {
      this.$openvidu.sendChat(this.inputText)
      this.inputText = ''
    }
  },

	/* Lifecycles */
	mounted() {}
}
</script>
