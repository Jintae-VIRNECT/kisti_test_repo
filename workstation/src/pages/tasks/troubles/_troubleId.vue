<template>
  <el-dialog
    id="trouble-info-modal"
    class="result-modal"
    :visible.sync="showMe"
    :title="$t('troubles.detail')"
    width="460px"
    top="11vh"
    @close="closed"
  >
    <div v-if="trouble.photoFilePath" class="image-container">
      <el-image
        :src="trouble.photoFilePath"
        :preview-src-list="[trouble.photoFilePath]"
      />
      <i class="el-icon-full-screen" />
    </div>
    <dl>
      <dt>{{ $t('troubles.list.column.user') }}</dt>
      <dd class="column-user">
        <div class="avatar">
          <div
            class="image"
            :style="`background-image: url(${trouble.workerProfile})`"
          />
        </div>
        <span>{{ trouble.workerName }}</span>
      </dd>
      <dt>{{ $t('troubles.list.column.reportedDate') }}</dt>
      <dd>{{ trouble.reportedDate }}</dd>
      <dt>{{ $t('troubles.list.column.content') }}</dt>
      <dd>{{ trouble.caption }}</dd>
    </dl>
  </el-dialog>
</template>

<script>
import resultService from '@/services/result'

export default {
  async asyncData({ params }) {
    return {
      trouble: await resultService.getTroubleDetail(params.troubleId),
    }
  },
  data() {
    return {
      showMe: true,
    }
  },
  methods: {
    closed() {
      this.showMe = false
      this.$router.replace('/tasks/troubles')
    },
  },
}
</script>
