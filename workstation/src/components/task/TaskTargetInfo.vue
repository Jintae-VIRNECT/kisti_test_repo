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
      <dt>{{ $t('task.targetInfo.capacity') }}</dt>
      <dd></dd>
      <dt>{{ $t('task.targetInfo.createdDate') }}</dt>
      <dd>{{ taskInfo.createdDate }}</dd>
      <el-divider />
      <dt>{{ $t('task.targetInfo.targetType') }}</dt>
      <dd></dd>
    </dl>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'

export default {
  mixins: [modalMixin],
  props: {
    task: Object,
  },
  data() {
    return {
      taskInfo: {},
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
#task-target-info-modal {
}
</style>
