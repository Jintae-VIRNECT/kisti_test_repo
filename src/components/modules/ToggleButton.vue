<template>
  <button
    class="toggle-button" 
    :class="[{ 'active': !active }, customClass]"
    :style="{
      'background': 'url('+src+') center no-repeat',
      'width': size+'px',
      'height': size+'px'
    }"
    @click="$emit('action', $event)"
  >{{ description }}</button>
</template>

<script>
export default {
	name: "ToggleButton",
	components: {},
	data() {
		return {
    }
	},
	props: {
    customClass: {
      type: String,
      default: ''
    },
    description: String,
    toggle: {
      type: Boolean,
      default: true
    },
    activeSrc: String,
    inactiveSrc: {
      type: String,
      default: null
    },
    active: {
      type: Boolean,
      default: true
    },
    size: {
      type: Number,
      default: 12
    }
  },
	computed: {
    src() {
      if(this.toggle) {
        if(this.active)
          return this.activeSrc
        else
          return this.inactiveSrc
      } else {
        return this.activeSrc
      }
    }
  },
	methods: {
  },

	/* Lifecycles */
	mounted() {}
}
</script>

<style scoped lang="scss">
@import "~assets/style/mixin";
.toggle-button {
  @include ir();
}
</style>