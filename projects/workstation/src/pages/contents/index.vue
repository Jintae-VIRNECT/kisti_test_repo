<template>
  <div id="contents">
    <h3>total: {{ contentsStatistics.totalContents }}</h3>
    <h3>managedTotal: {{ contentsStatistics.totalManagedContents }}</h3>
    <search-tab-nav
      :filter="searchFilter"
      :sort="searchSort"
      @submit="searchContents"
    />
    <div v-for="content in contents" :key="content.id">
      <p>
        <nuxt-link :to="`/contents/${content.id}`">{{ content }}</nuxt-link>
      </p>
    </div>
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
      contents: await contentService.getDefaultContentsList(),
      contentsStatistics: await contentService.getContentsStatistics(),
    }
  },
  methods: {
    async searchContents(params) {
      this.contents = await contentService.searchContents(params)
    },
  },
}
</script>
