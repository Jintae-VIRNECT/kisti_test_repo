<template>
  <el-dialog
    id="set-task-manage-modal"
    class="info-modal"
    :visible.sync="showMe"
    :title="$t('task.new.title')"
    width="860px"
    top="11vh"
  >
    <el-row type="flex" v-if="content">
      <el-col :span="12">
        ㅁㄴ유
      </el-col>
      <el-col :span="12">
        1234
      </el-col>
    </el-row>
    <el-row class="btn-wrapper">
      <el-button @click="$emit('next', content)" type="primary">
        {{ $t('task.new.next') }}
      </el-button>
    </el-row>
  </el-dialog>
</template>

<script>
import contentService from '@/services/content'
import { sharedStatus } from '@/models/content/Content'
import filters from '@/mixins/filters'

export default {
  mixins: [filters],
  props: {
    visible: Boolean,
    contentInfo: Object,
  },
  data() {
    return {
      showMe: false,
      content: this.contentInfo,
      properties: null,
      sharedStatus,
      propertiesProps: {
        label: 'label',
        childern: 'childern',
      },
    }
  },
  computed: {
    shared() {
      return this.$t(
        sharedStatus.find(status => status.value === this.content.shared).label,
      )
    },
  },
  watch: {
    visible(bool) {
      this.showMe = bool
    },
    async showMe(bool) {
      if (!bool) {
        this.$emit('update:visible', bool)
        return false
      }
      const promise = {
        content: contentService.getContentInfo(this.contentId),
        properties: contentService.getContentProperties(
          this.contentId,
          this.$store.getters['auth/myProfile'].uuid,
        ),
      }
      this.content = await promise.content
      this.properties = await promise.properties
    },
  },
  methods: {},
}
</script>

<style lang="scss">
#set-task-info-modal .el-dialog__body {
  .el-row:first-child {
    height: calc(100% - 58px);
  }
  .el-row.btn-wrapper {
    height: auto;
    margin: 24px 0;
    .el-button:first-child {
      width: 270px;
    }
    .el-button:last-child {
      float: right;
      width: 92px;
    }
  }
}
</style>
