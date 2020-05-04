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
    <p v-for="content in contentsList" :key="content.id">
      <nuxt-link :to="`/contents/${content.id}`">{{ content }}</nuxt-link>
    </p>
    <el-row type="flex" justify="center">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="contentsTotal"
        @current-change="contentsPageChange"
      />
    </el-row>
  </div>
</template>

<script>
// models
import {
  filter as searchFilter,
  sort as searchSort,
} from '@/models/content/Content'
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
      contentsSearchParams: {},
    }
  },
  async asyncData() {
    const promise = {
      contents: contentService.searchContents(),
      contentStatistics: contentService.getContentStatistics(),
    }
    return {
      contentsList: (await promise.contents).list,
      contentsTotal: (await promise.contents).total,
      contentStatistics: await promise.contentStatistics,
    }
  },
  methods: {
    async searchContents(params) {
      const contents = await contentService.searchContents(params)
      this.contentsList = contents.list
      this.contentsTotal = contents.total
      this.contentsSearchParams = params
    },
    async contentsPageChange(page) {
      const params = { page, ...this.contentsSearchParams }
      const contents = await contentService.searchContents(params)
      this.contentsList = contents.list
    },
  },
}
</script>
