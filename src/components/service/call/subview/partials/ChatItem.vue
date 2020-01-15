<template>
	<li class="chat-item" :class="type">
    <img class="profile"/>
    <div class="chat-item__body">
      <div class="chatbox">
        <span class="name">{{ chat.name }}</span>
        <p 
          class="text" 
          :class="{ 'alarm': type === 'system' && chat.name === 'alarm'
                  , 'people': type === 'system' && chat.name === 'people' }"
        >{{ chat.text }}</p>
      </div>
      <span class="time">{{ $moment(chat.date).calendar(null, { sameElse: 'L A h:mm'}) }}</span>
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
    }
  },
	watch: {},
	methods: {},

	/* Lifecycles */
	mounted() {}
}
</script>
