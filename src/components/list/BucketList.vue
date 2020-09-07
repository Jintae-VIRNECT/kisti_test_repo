<template>
  <div class="bucket-list">
    <div
      v-for="(bucket, index) in buckets"
      class="file-manage__row"
      :key="index"
      @click="$emit('selectbucket', bucket.name)"
    >
      <div class="file-manage__row name">
        {{ bucket.name }}
      </div>
      <div class="file-manage__row size"></div>
      <div class="file-manage__row lastmod"></div>
      <div class="file-manage__row tools">
        <tool-delete :bucket="bucket.name"></tool-delete>
      </div>
    </div>
  </div>
</template>

<script>
import ToolDelete from 'components/tools/ToolDelete'
export default {
  name: 'BucketList',
  components: {
    ToolDelete,
  },
  props: {
    buckets: {
      type: Array,
      default: () => [],
    },
  },
}
</script>

<style lang="scss"></style>
