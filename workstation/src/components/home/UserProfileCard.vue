<template>
  <el-card class="member-profile-card">
    <div slot="header">
      <h3>{{ $t('home.profile.title') }}</h3>
      <router-link to="/">{{ $t('home.profile.link') }}</router-link>
    </div>
    <div>
      <span>{{ $t('home.profile.usingNickname') }}</span>
      <div>
        <div class="avatar">
          <div
            class="image"
            :style="`background-image: url(${myProfile.image})`"
          />
        </div>
        <span class="name">{{ myProfile.nickname }}</span>
        <span class="email">{{ myProfile.email }}</span>
      </div>
      <span>{{ $t('home.profile.workspaceRole') }}</span>
      <div class="column-role">
        <el-tag :class="myRole">{{ myRole }}</el-tag>
      </div>
      <span>{{ $t('home.profile.usingPlans') }}</span>
      <div></div>
    </div>
  </el-card>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  computed: {
    ...mapGetters({
      myProfile: 'auth/myProfile',
      myWorkspaces: 'auth/myWorkspaces',
      activeWorkspace: 'workspace/activeWorkspace',
    }),
    myRole() {
      return this.myWorkspaces.find(
        workspace => workspace.uuid === this.activeWorkspace.info.uuid,
      ).role
    },
  },
}
</script>

<style lang="scss">
.member-profile-card {
  a {
    display: block;
  }
  .el-card__body > div {
    & > span {
      display: block;
      margin: 24px 0 12px;
      color: #5e6b81;
      font-size: 12px;

      &:first-child {
        margin-top: 0;
      }
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
}
</style>
