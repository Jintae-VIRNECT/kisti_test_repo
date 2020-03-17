<template>
  <div id="contents">
    <div v-for="content in contents" :key="content.id">
      <p>
        <nuxt-link :to="`/contents/${content.id}`">{{ content }}</nuxt-link>
      </p>
    </div>
  </div>
</template>

<script>
import contentService from '@/services/content'

export default {
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
