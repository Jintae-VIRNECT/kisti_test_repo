<template>
<li class="sharing-image">
  <div
    v-if="!isPdf"
    class="sharing-image"
  >
    <img src=""/>
    <span>파일명.png</span>
  </div>
  <button
    v-else
    class="sharing-image"
  >
    <img src=""/>
    <span>파일명.png</span>
  </button>
</li>
</template>

<script>
export default {
	name: "SharingImage",
	components: {
  },
	data() {
		return {
      ifPdf: false
    }
	},
	computed: {},
	watch: {},
	methods: {},

	/* Lifecycles */
	mounted() {}
}
</script>
