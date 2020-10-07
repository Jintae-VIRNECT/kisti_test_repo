<template>
  <button @click="downloadObject" class="tool-button">
    <img src="~assets/image/ic_down_on.svg" />
  </button>
</template>

<script>
export default {
  name: 'ToolDownload',
  props: {
    objectName: {
      type: String,
    },
  },
  methods: {
    downloadObject() {
      this.$eventBus.$emit('download::object', this.objectName)
    },
  },
}
</script>

<style src="assets/style/tools.scss" lang="scss"></style>
