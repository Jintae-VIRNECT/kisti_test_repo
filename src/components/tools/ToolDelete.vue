<template>
  <button @click="del" class="tool-button">
    <img src="~assets/img/ic_delete_w.svg" />
  </button>
</template>

<script>
export default {
  name: 'ToolDelete',
  props: {
    objectName: {
      type: String,
      default: null,
    },
    bucketName: {
      type: String,
      default: null,
    },
  },
  methods: {
    del() {
      console.log(this.objectName)
      this.objectName
        ? this.$eventBus.$emit('delete::object', this.objectName)
        : this.$eventBus.$emit('delete::bucket', this.bucketName)
    },
  },
}
</script>

<style src="assets/style/tools.scss" lang="scss"></style>
