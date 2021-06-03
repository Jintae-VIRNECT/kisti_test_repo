<template>
  <div class="workspace-info">
    <VirnectThumbnail
      :size="72"
      :image="workspace.profile"
      :defaultImage="$defaultWorkspaceProfile"
    />
    <dl>
      <dt>{{ $t('home.workspace.name') }}</dt>
      <dd class="workspace-name">{{ workspace.name }}</dd>
      <dt>{{ $t('home.workspace.desc') }}</dt>
      <dd>
        {{ workspace.description || $t('home.workspace.descEmpty') }}
      </dd>
      <dt>{{ $t('home.workspace.master') }}</dt>
      <dd class="column-user">
        <VirnectThumbnail :image="myProfile.profile" />
        <span>{{ myProfile.nickname }}</span>
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
import { mapGetters } from 'vuex'
import workspaceService from '@/services/workspace'

export default {
  data() {
    return {
      workspace: {},
    }
  },
  computed: {
    ...mapGetters({
      myProfile: 'auth/myProfile',
    }),
  },
  mounted() {
    this.workspace = workspaceService.getMasterWorkspaceInfo()
  },
}
</script>

<style lang="scss">
.workspace-info {
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
  .column-user > span {
    display: inline-block;
    max-width: 80%;
    overflow: hidden;
    white-space: pre;
    text-overflow: ellipsis;
  }
  .el-button {
    width: 100%;
    height: 36px;
    margin: 28px 0 10px;
  }
}
</style>
