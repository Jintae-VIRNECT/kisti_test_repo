<template>
  <div id="contents">
    <h2>Contents</h2>
    <h3>total: {{ contentStatistics.totalContents }}</h3>
    <h3>managed: {{ contentStatistics.totalManagedContents }}</h3>
    <search-tab-nav
      :filter="searchFilter"
      :sort="searchSort"
      @submit="searchContents"
    />
    <p v-for="content in contents" :key="content.id">
      <nuxt-link :to="`/contents/${content.id}`">{{ content }}</nuxt-link>
    </p>
  </div>
</template>

<script>
// models
import { filter as searchFilter, sort as searchSort } from '@/models/content'
// services
import contentService from '@/services/content'
// components
import SearchTabNav from '@/components/common/SearchTabNav'

export default {
  components: {
    SearchTabNav,
  },
  data() {
    return {
      searchFilter,
      searchSort,
    }
  },
  async asyncData() {
    return {
      contents: await contentService.searchContents(),
      contentStatistics: await contentService.getContentStatistics(),
    }
  },
  methods: {
    async searchContents(params) {
      this.contents = await contentService.searchContents(params)
    },
  },
}
</script>
