<template>
  <el-dialog
    id="paper-modal"
    :visible.sync="showMe"
    :title="$t('results.paperDetail.title')"
    width="540px"
    top="11vh"
    @close="closed"
  >
    <el-row v-for="(action, index) in paper.paperActions" :key="action.id">
      <el-col>
        {{ $tc('results.paperDetail.item', index + 1) }}
      </el-col>
      <el-col>
        <dl>
          <dt>{{ action.title }}</dt>
          <dd>{{ action.answer }}</dd>
        </dl>
      </el-col>
    </el-row>
  </el-dialog>
</template>

<script>
import resultService from '@/services/result'

export default {
  async asyncData({ params }) {
    return {
      paper: await resultService.getPaperDetail(params.paperId),
    }
  },
  data() {
    return {
      showMe: true,
    }
  },
  methods: {
    closed() {
      const { taskId, subTaskId } = this.paper
      this.showMe = false
      this.$router.replace(`/tasks/${taskId}/${subTaskId}`)
    },
  },
}
</script>

<style lang="scss">
#__nuxt #contents-info-modal .el-dialog__body {
  height: 700px;
}
</style>
