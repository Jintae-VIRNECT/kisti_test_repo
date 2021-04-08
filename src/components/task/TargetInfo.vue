<template>
  <el-dialog
    id="task-target-info-modal"
    class="result-modal"
    :visible.sync="showMe"
    :title="$t('task.targetInfo.title')"
    width="320px"
    top="11vh"
  >
    <dl>
      <dt>{{ $t('task.targetInfo.id') }}</dt>
      <dd>{{ taskInfo.id }}</dd>
      <dt>{{ $t('task.targetInfo.name') }}</dt>
      <dd>{{ taskInfo.name }}</dd>
      <el-row type="flex">
        <el-col>
          <dt>{{ $t('task.targetInfo.capacity') }}</dt>
          <dd>{{ taskInfo.contentSize | byte2mb }}</dd>
        </el-col>
        <el-col>
          <dt>{{ $t('task.targetInfo.createdDate') }}</dt>
          <dd>{{ taskInfo.createdDate | localDateFormat }}</dd>
        </el-col>
      </el-row>
      <el-divider />
      <dt>{{ $t('task.targetInfo.targetType') }}</dt>
      <dd>
        <span>{{ targetType2label(taskInfo.targetType) }}</span>
        <el-tag class="content-size">
          {{ taskInfo.targetSize }}x{{ taskInfo.targetSize }}
        </el-tag>
        <img
          v-if="taskInfo.target.imgPath"
          src="~assets/images/icon/ic-print.svg"
          @click="print(cdn(taskInfo.target.imgPath), taskInfo.targetSize)"
        />
        <img
          v-if="taskInfo.target.imgPath"
          src="~assets/images/icon/ic-file-download.svg"
          @click="
            download(
              cdn(taskInfo.target.imgPath),
              `task_${taskInfo.id}_${taskInfo.name}`,
            )
          "
        />
      </dd>
    </dl>
    <div class="qr">
      <img :src="taskInfo.target.imgPath" />
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
import filters from '@/mixins/filters'
import utils from '@/mixins/utils'

export default {
  mixins: [modalMixin, filters, utils],
  props: {
    task: Object,
  },
  data() {
    return {
      taskInfo: {
        target: {},
      },
    }
  },
  methods: {
    opened() {
      this.taskInfo = this.task
    },
  },
}
</script>

<style lang="scss">
#__nuxt #task-target-info-modal {
  dl {
    padding-bottom: 0;
  }
  .el-row dd {
    margin-bottom: 0;
  }
  .qr img {
    width: 100%;
  }
  .content-size {
    height: 24px;
    margin-left: 4px;
    color: $font-color-desc;
    line-height: 22px;
    background: transparent;
    border-color: $font-color-desc;
  }
}
</style>
