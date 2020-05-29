<template>
  <el-dialog
    id="paper-modal"
    class="result-modal"
    :visible.sync="showMe"
    :title="$t('results.paperDetail.title')"
    width="540px"
    top="11vh"
    @close="closed"
  >
    <el-row v-for="(action, index) in paper.paperActions" :key="action.id">
      <el-col :span="4">
        <p>
          {{ $tc('results.paperDetail.item', index + 1) }}
        </p>
      </el-col>
      <el-col :span="20">
        <dl>
          <dt>{{ action.title }}</dt>
          <dd>
            <p>{{ action.answer }}</p>
            <a
              v-if="action.photoFilePath"
              class="el-button"
              href="action.photoFilePath"
              download
            >
              <img src="~assets/images/icon/ic-download.svg" />
              <span>{{ $t('common.download') }}</span>
            </a>
          </dd>
        </dl>
      </el-col>
      <el-col v-if="action.photoFilePath" class="image-container">
        <el-image
          :src="action.photoFilePath"
          :preview-src-list="[action.photoFilePath]"
        />
        <i class="el-icon-full-screen" />
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
    download(path) {
      window.open(path)
    },
  },
}
</script>

<style lang="scss">
#__nuxt #paper-modal .el-dialog__body {
  .el-row {
    margin: 12px 30px;
    padding: 16px 0;
    border-bottom: solid 1px #e6e9ee;
  }
  .el-row:last-child {
    border-bottom: none;
  }
  .image-container {
    padding: 0;
    border-bottom: none;
  }

  dt {
    margin-bottom: 10px;
  }
  p {
    color: $font-color-desc;
  }
  .el-button {
    display: inline-block;
    margin: 22px 0 30px;
    padding: 2px 10px 2px 7px;
  }
}
</style>
