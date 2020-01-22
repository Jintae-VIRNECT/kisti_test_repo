<template>
	<li class="chat-item" :class="type">
    <img class="profile" src="~assets/image/call/chat_img_user.png" v-if="!hideProfile"/>
    <div class="chat-item__body" :class="{ 'hidden': hideProfile }">
      <div class="chatbox">
        <span class="name">{{ chat.name }}</span>
        <p 
          class="text" 
          :class="{ 'alarm': type === 'system' && chat.name === 'alarm'
                  , 'people': type === 'system' && chat.name === 'people' }"
          v-html="chat.text"
        ></p>
      </div>
      <span v-if="!hideTime" class="time">{{ $moment(chat.date).format('A hh:mm') }}</span>
      <!-- <span v-if="!hideTime" class="time">{{ chat.date }}</span> -->
    </div>
  </li>
</template>

<script>
export default {
	name: "ChatItem",
	components: {},
	data() {
		return {}
  },
  props: {
    beforeChat: Object,
    afterChat: Object,
    chat: Object
  },
	computed: {
    type () {
      if(false === this.chat.type) {
        return 'opponent';
      } else if(this.chat.type === 'me') {
        return 'me';
      } else {
        return 'system';
      }
    },
    hideTime() {
      if (this.afterChat === null) {
        return false
      }

      if(this.afterChat.type !== this.chat.type) {
        return false
      }

      if(this.$moment(this.afterChat.date).format('A hh:mm')
         !== this.$moment(this.chat.date).format('A hh:mm')) {
        return false
      }

      if(this.afterChat.nodeId !== this.chat.nodeId) {
        return false
      }

      if (this.chat.nodeId === null && this.chat.name === "alarm") {
        return false
      }

      return true
    },
    hideProfile() {
      if(this.beforeChat === null) {
        return false
      }
      
      if(this.beforeChat.type === this.chat.type && this.beforeChat.nodeId === this.chat.nodeId) {
        return true
      }

      return false
    }
  },
	watch: {},
	methods: {},

	/* Lifecycles */
	mounted() {}
}
</script>
