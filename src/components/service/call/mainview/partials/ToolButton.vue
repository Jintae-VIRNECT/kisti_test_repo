<template>
<button 
  class="main-tools__button"
  :class="{ 'active': active }"
  @click="$emit('action')">
  <img v-if="onActive" :src="activeSrc"/>
  <img v-else :src="src"/>
  <span>{{ text }}</span>
</button>
</template>

<script>
export default {
	name: "MainTools",
	components: {},
	data() {
		return {}
	},
	props: {
    text: String,
    active: {
      type: Boolean,
      default: false
    },
    src: String,
    onActive: {
      type: Boolean,
      default: false
    },
    activeSrc: {
      type: String,
      default: null
    }
  },
	watch: {},
	methods: {
  },

  /* Lifecycles */
  beforeDestroy() {
  },
	mounted() {
  }
}
</script>
