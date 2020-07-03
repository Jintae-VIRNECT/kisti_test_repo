<template>
  <div class="workspace-info">
    <div class="avatar">
      <div
        class="image"
        :style="`background-image: url('${workspace.profile}')`"
      />
    </div>
    <dl>
      <dt>{{ $t('home.workspace.name') }}</dt>
      <dd class="workspace-name">{{ workspace.name }}</dd>
      <dt>{{ $t('home.workspace.desc') }}</dt>
      <dd>
        {{ workspace.description || $t('home.workspace.descEmpty') }}
      </dd>
      <dt>{{ $t('home.workspace.master') }}</dt>
      <dd class="column-user">
        <div class="avatar">
          <div
            class="image"
            :style="`background-image: url('${workspace.masterProfile}')`"
          />
        </div>
        <span>{{ workspace.masterNickName }}</span>
      </dd>
      <a :href="`${$url.workstation}/workspace/${workspace.uuid}`">
        <el-button type="simple">
          {{ $t('common.workstation') }}
        </el-button>
      </a>
    </dl>
  </div>
</template>

<script>
import workspaceService from '@/services/workspace'

export default {
  data() {
    return {
      workspace: {},
    }
  },
  async beforeMount() {
    this.workspace = workspaceService.getMasterWorkspaceInfo()
  },
}
</script>

<style lang="scss">
.workspace-info {
  .avatar {
    width: 72px;
    height: 72px;
    margin: 0;
  }
  dt {
    margin-top: 20px;
    color: $font-color-desc;
    font-size: 12px;
  }
  dd {
    margin-top: 8px;
    word-break: break-word;
  }
  dd.workspace-name {
    margin-top: 4px;
    font-size: 20px;
  }
  .el-button {
    width: 100%;
    height: 36px;
    margin: 28px 0 10px;
  }
}
</style>
