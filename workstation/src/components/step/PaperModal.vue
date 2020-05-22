<template>
  <el-dialog
    class="paper-modal"
    :visible.sync="showMe"
    :title="$t('members.kick.title')"
    width="440px"
    top="11vh"
  >
    <div>
      {{ $t('members.kick.desc') }}
    </div>
    <div slot="footer">
      <el-button @click="$emit('back')">
        {{ $t('members.kick.back') }}
      </el-button>
      <el-button type="danger" @click="submit">
        {{ $t('members.kick.submit') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
import workspaceService from '@/services/workspace'

export default {
  mixins: [modalMixin],
  props: {
    data: Object,
  },
  methods: {
    async submit() {
      try {
        await workspaceService.kickMember(this.data.uuid)
        this.$message.success({
          message: this.$t('members.kick.message.kickSuccess'),
          showClose: true,
        })
        this.$emit('kicked')
      } catch (e) {
        this.$message.error({
          message: this.$t('members.kick.message.kickFail'),
          showClose: true,
        })
      }
    },
  },
}
</script>

<style lang="scss">
.member-kick-modal {
  .el-dialog__footer {
    border-top: solid 1px #edf0f7;

    .el-button:first-child {
      float: left;
    }
  }
}
</style>
