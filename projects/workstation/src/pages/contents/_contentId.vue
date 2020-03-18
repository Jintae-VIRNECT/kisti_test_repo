<template>
  <div id="_contentId">
    <h3>{{ content }}</h3>
    <el-button @click="deleteContent(content.id)">삭제</el-button>
    <p v-for="sceneGroup in sceneGroups" :key="sceneGroup.id">
      {{ sceneGroup }}
    </p>
  </div>
</template>

<script>
import contentService from '@/services/content'

export default {
  async asyncData({ params }) {
    return {
      content: await contentService.getContentInfo(params.contentId),
      sceneGroups: await contentService.getSceneGroupList(params.contentId),
    }
  },
  methods: {
    async deleteContent(contentId) {
      try {
        await contentService.deleteContent(contentId)
        // 성공
        this.$notify.success({
          title: 'Success',
          message: '삭제 성공',
        })
        this.$router.push('/contents')
      } catch (e) {
        // 실패
        this.$notify.error({
          title: 'Error',
          message: e,
        })
      }
    },
  },
}
</script>
