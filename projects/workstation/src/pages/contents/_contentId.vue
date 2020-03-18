<template>
  <div id="_contentId">
    <h3>{{ content }}</h3>
    <el-button @click="deleteContent(content.id)">
      {{ $t('buttons.delete') }}
    </el-button>
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
      sceneGroups: await contentService.getSceneGroupsList(params.contentId),
    }
  },
  methods: {
    async deleteContent(contentId) {
      try {
        await this.$confirm(this.$t('questions.deleteConfirm'))
      } catch (e) {
        return false
      }

      try {
        await contentService.deleteContent(contentId)
        // 성공
        this.$notify.success({
          title: 'Success',
          message: this.$t('messages.deleteSuccess'),
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
