<template>
  <el-card class="user-profile-card">
    <div slot="header">
      <h3>{{ $t('home.profile.title') }}</h3>
      <a :href="profileUpdatePage" target="_blank">
        {{ $t('home.profile.link') }}
      </a>
    </div>
    <dl>
      <dt>{{ $t('home.profile.usingNickname') }}</dt>
      <dd>
        <div class="avatar">
          <div
            class="image"
            :style="`background-image: url(${myProfile.image})`"
          />
        </div>
        <span class="name">{{ myProfile.nickname }}</span>
        <span class="email">{{ myProfile.email }}</span>
      </dd>
      <dt>{{ $t('home.profile.workspaceRole') }}</dt>
      <dd class="column-role">
        <el-tag :class="myRole">{{ myRole }}</el-tag>
      </dd>
      <dt>{{ $t('home.profile.usingPlans') }}</dt>
      <dd>-</dd>
    </dl>
  </el-card>
</template>

<script>
import { mapGetters } from 'vuex'
import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'

export default {
  data() {
    return {
      profileUpdatePage: `${urls.account[process.env.TARGET_ENV]}/profile`,
    }
  },
  computed: {
    ...mapGetters({
      myProfile: 'auth/myProfile',
      myWorkspaces: 'auth/myWorkspaces',
      activeWorkspace: 'workspace/activeWorkspace',
    }),
    myRole() {
      return this.myWorkspaces.find(
        workspace => workspace.uuid === this.activeWorkspace.uuid,
      ).role
    },
  },
}
</script>

<style lang="scss">
.user-profile-card {
  a {
    display: block;
  }
  dt {
    display: block;
    margin: 24px 0 12px;
    color: #5e6b81;
    font-size: 12px;

    &:first-child {
      margin-top: 0;
    }
  }
  .avatar {
    float: left;
    width: 40px;
    height: 40px;
    margin-right: 12px;
  }
  .name {
    display: block;
    line-height: 20px;
  }
  .email {
    display: block;
    color: $font-color-desc;
    font-size: 12px;
    line-height: 20px;
  }
  .column-role {
    margin-bottom: 28px;
  }
}
</style>
