<template>
  <div class="object-list">
    <div
      v-for="(object, index) in objects"
      class="file-manage__row"
      :key="index"
    >
      <div
        class="file-manage__row name"
        @click="$emit('selectobject', object.prefix)"
      >
        {{ object.prefix ? object.prefix : object.name }}
      </div>
      <div class="file-manage__row size">{{ object.size | convertSize }}</div>
      <div class="file-manage__row lastmod">
        {{ $dayjs(object.lastModified).format('YY/MM/DD hh:mm') }}
      </div>
      <div class="file-manage__row tools">
        <tool-download :objectName="object.name"></tool-download>
        <tool-delete :objectName="object.name"></tool-delete>
      </div>
    </div>
  </div>
</template>

<script>
import ToolDelete from 'components/tools/ToolDelete'
import ToolDownload from 'components/tools/ToolDownload'
export default {
  name: 'ObjectList',
  components: {
    ToolDelete,
    ToolDownload,
  },
  props: {
    objects: {
      type: Array,
      default: () => [],
    },
  },
  filters: {
    convertSize(size) {
      const mb = 1048576

      if (!size) {
        return ''
      }

      if (size >= mb) {
        size = size / 1024 / 1024
        return `${size.toFixed(1)}MB`
      } else {
        size = size / 1024
        return `${size.toFixed(1)}KB`
      }
    },
  },
}
</script>

<style lang="scss"></style>
