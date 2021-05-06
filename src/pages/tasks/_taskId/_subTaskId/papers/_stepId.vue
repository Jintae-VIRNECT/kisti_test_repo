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
    <el-collapse v-model="activePapers" accordion>
      <el-collapse-item
        v-for="(paper, index) in papers"
        :key="paper.paperId"
        :name="paper.paperId"
      >
        <template slot="title">
          <span>{{ $t('results.paper') }} {{ papers.length - index }}</span>
          <el-divider direction="vertical" />
          <span>{{ paper.reportedDate | localTimeFormat }}</span>
        </template>
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
                  :href="cdn(action.photoFilePath)"
                  download
                  target="_blank"
                >
                  <img src="~assets/images/icon/ic-download.svg" />
                  <span>{{ $t('common.download') }}</span>
                </a>
              </dd>
            </dl>
          </el-col>
          <el-col v-if="action.photoFilePath" class="image-container">
            <el-image
              :src="cdn(action.photoFilePath)"
              :preview-src-list="[cdn(action.photoFilePath)]"
            />
            <i class="el-icon-full-screen" />
          </el-col>
        </el-row>
      </el-collapse-item>
    </el-collapse>
  </el-dialog>
</template>

<script>
import resultService from '@/services/result'
import filters from '@/mixins/filters'

export default {
  mixins: [filters],
  async asyncData({ params }) {
    const { list } = await resultService.searchPapers({
      stepId: params.stepId,
      size: 50,
    })
    return {
      papers: list,
      activePapers: [list[0].paperId],
    }
  },
  data() {
    return {
      showMe: true,
    }
  },
  methods: {
    closed() {
      const { taskId, subTaskId } = this.papers[0]
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
  .el-collapse-item__header {
    padding-left: 47px;
    .el-collapse-item__arrow {
      left: 26px;
    }
  }
  .el-collapse-item__content {
    padding-bottom: 0;
  }
  .el-row {
    margin: 0 30px;
    padding: 16px 0;
    border-bottom: solid 1px #e6e9ee;
  }
  .el-row:last-child {
    border-bottom: none;
  }
  .image-container {
    margin-top: 20px;
    padding: 0;
    border-bottom: none;
  }
  .el-col dl {
    padding: 0;
  }
  dt {
    margin-bottom: 10px;
    font-size: 14px;
  }
  dd {
    margin-bottom: 0;
  }
  p {
    color: $font-color-desc;
  }
  .el-button {
    display: inline-block;
    margin: 8px 0;
    padding: 2px 10px 2px 7px;
  }
  .image-container > .el-icon-full-screen {
    top: 0;
    right: 0;
  }
}
</style>
