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
          <dd></dd>
        </el-col>
        <el-col>
          <dt>{{ $t('task.targetInfo.createdDate') }}</dt>
          <dd>{{ taskInfo.createdDate | localDateFormat }}</dd>
        </el-col>
      </el-row>
      <el-divider />
      <dt>{{ $t('task.targetInfo.targetType') }}</dt>
      <dd>
        <span>{{ taskInfo.targets[0].type }}</span>
        <img
          v-if="taskInfo.targets[0].imgPath"
          src="~assets/images/icon/ic-print.svg"
          @click="print(taskInfo.targets[0].imgPath)"
        />
        <img
          v-if="taskInfo.targets[0].imgPath"
          src="~assets/images/icon/ic-file-download.svg"
          @click="download(taskInfo.targets[0].imgPath)"
        />
      </dd>
    </dl>
    <div class="qr">
      <img :src="taskInfo.targets[0].imgPath" />
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
import filters from '@/mixins/filters'

export default {
  mixins: [modalMixin, filters],
  props: {
    task: Object,
  },
  data() {
    return {
      taskInfo: {
        targets: [{}],
      },
    }
  },
  methods: {
    opened() {
      this.taskInfo = this.task
    },
    download(url) {
      window.open(url)
    },
    print(url) {
      const popup = window.open('', '_blank')
      popup.document.write(`<img src="${url}" />`)
      popup.document.close()
      popup.print()
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
}
</style>
