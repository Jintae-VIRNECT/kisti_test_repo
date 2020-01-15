<template>
	<div class="subview-wrapper">
    <!-- <sub-video></sub-video> -->
    <chat-section></chat-section>
  </div>
</template>

<script>
import SubVideo from './SubVideo'
import ChatSection from './ChatView'
export default {
	name: "SubViewLayout",
	components: {
    SubVideo,
    ChatSection
  },
	data() {
		return {}
	},
	computed: {},
	watch: {},
	methods: {},

	/* Lifecycles */
	mounted() {}
}
</script>
