<template>
  <el-dialog
    class="workspace-leave-modal"
    :visible.sync="showMe"
    :title="$t('workspace.setting.leave')"
    width="460px"
    top="11vh"
  >
    <div>
      <div class="workspace-leave-info">
        <dl>
          <dt>{{ $t('workspace.setting.name') }}</dt>
          <dd>{{ activeWorkspace.name }}</dd>
          <dt>{{ $t('workspace.master') }}</dt>
          <dd class="column-user">
            <div class="avatar">
              <div
                class="image"
                :style="
                  `background-image: url(${activeWorkspace.masterProfile})`
                "
              />
            </div>
            <span>{{ activeWorkspace.masterName }}</span>
          </dd>
        </dl>
      </div>
      <p class="leave-text">{{ $t('workspace.setting.leaveInfo.title') }}</p>
      <ul>
        <li
          v-for="(notice, idx) of $t('workspace.setting.leaveInfo.list')"
          :key="idx"
        >
          {{ notice }}
        </li>
      </ul>
      <el-checkbox v-model="leaveCheck">{{
        $t('workspace.setting.leaveInfo.agree')
      }}</el-checkbox>
    </div>
    <div slot="footer">
      <el-button :disabled="!leaveCheck" @click="leaveWorkspace">
        {{ $t('workspace.setting.leave') }}
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
    activeWorkspace: Object,
    myProfile: Object,
  },
  data() {
    return {
      leaveCheck: false,
    }
  },
  methods: {
    async leaveWorkspace() {
      try {
        await workspaceService.workspaceLeave(this.myProfile.uuid)
        this.$message.success({
          message: this.$t('workspace.setting.leaveInfo.success'),
          duration: 2000,
          showClose: true,
        })
        this.$router.push('/')
      } catch (e) {
        this.$message.error({
          message: this.$t('workspace.setting.leaveInfo.fail') + `\n(${e})`,
          duration: 2000,
          showClose: true,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .workspace-leave-modal {
  .el-dialog__body {
    overflow-y: scroll;
  }
  p {
    letter-spacing: -0.3px;
    & > span,
    & > img {
      vertical-align: middle;
    }
  }
  .el-divider {
    margin: 24px 0;
  }
  .el-dialog__footer {
    text-align: right;
    border-top: solid 1px #edf0f7;
  }
  .workspace-leave-info {
    margin-bottom: 26px;
    padding: 14px 16px;
    background-color: #f5f7fa;
  }
  dt {
    margin-bottom: 4px;
    color: #5e6b81;
    font-size: 12px;
  }
  dd {
    margin-bottom: 12px;
    font-size: 14px;
  }
  dd.column-user {
    margin-bottom: 0;
    span {
      font-size: 14px;
    }
  }
  .leave-text {
    font-size: 12.6px;
  }
  ul {
    margin-top: 10px;
    margin-bottom: 20px;
    li {
      position: relative;
      margin-bottom: 10px;
      padding-left: 24px;
      color: #6a7895;
      font-size: 12.6px;
      word-break: break-all;
      &:before {
        position: absolute;
        top: 8px;
        left: 3px;
        width: 6px;
        height: 6px;
        background: #6a7895;
        border-radius: 50%;
        content: '';
      }
    }
  }
  .el-checkbox {
    position: relative;
    width: 100%;
    white-space: inherit;
    .el-checkbox__input {
      position: absolute;
      top: 2px;
      left: 0;
    }
    .el-checkbox__label {
      padding-left: 24px;
      color: #0b1f48;
    }
  }
  .el-checkbox__input.is-checked .el-checkbox__inner,
  .el-checkbox__input.is-indeterminate .el-checkbox__inner {
    background-color: #1468e2;
    border-color: #1468e2;
  }
}
</style>
