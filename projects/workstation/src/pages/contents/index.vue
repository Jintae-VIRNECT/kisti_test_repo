<template>
  <div id="contents">
    <div v-for="content in contents" :key="content.id">
      <p>{{ content }}</p>
    </div>
  </div>
</template>

<script>
import contentService from '@/services/content'

export default {
  components: {},
  data() {
    return {
      contents: [],
    }
  },
  async asyncData() {
    return {
      contents: await contentService.getDefaultContentsList(),
    }
  },
}
</script>
