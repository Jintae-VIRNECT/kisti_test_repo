<template>
  <div class="workspace-info">
    <div class="info">
      <div class="avatar">
        <div
          class="image"
          :style="`background-image: url(${activeWorkspace.info.profile})`"
        />
      </div>
      <h5>{{ activeWorkspace.info.name }}</h5>
      <span>{{
        activeWorkspace.info.description ||
          $t('workspace.info.descriptionEmpty')
      }}</span>
    </div>
    <el-divider />
    <div class="members">
      <h6>{{ $t('workspace.info.title') }}</h6>
      <!-- 마스터 -->
      <span>{{ $t('workspace.master') }}</span>
      <span class="count">1</span>
      <div class="users">
        <el-tooltip :content="activeWorkspace.master.name">
          <div class="avatar">
            <div
              class="image"
              :style="
                `background-image: url(${activeWorkspace.master.profile})`
              "
            />
          </div>
        </el-tooltip>
      </div>
      <!-- 매니저 -->
      <span>{{ $t('workspace.manager') }}</span>
      <span class="count">{{ activeWorkspace.managers.length }}</span>
      <div class="users">
        <el-tooltip
          v-for="user in activeWorkspace.managers"
          :key="user.uuid"
          :content="user.name"
        >
          <div class="avatar">
            <img :src="user.profile" />
          </div>
        </el-tooltip>
      </div>
      <!-- 멤버 -->
      <span>{{ $t('workspace.member') }}</span>
      <span class="count">{{ activeWorkspace.members.length }}</span>
    </div>
    <el-divider />
    <div class="plans"></div>
    <el-button @click="addMember">
      {{ $t('workspace.info.addMember') }}
    </el-button>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  computed: {
    ...mapGetters({
      activeWorkspace: 'workspace/activeWorkspace',
    }),
  },
  methods: {
    addMember() {
      this.$router.push({
        path: '/members',
        query: { modal: 'memberAdd' },
      })
    },
  },
}
</script>

<style lang="scss">
.workspace-info {
  margin: 24px;

  .info {
    margin-bottom: 40px;
    text-align: center;

    & > .avatar {
      width: 72px;
      height: 72px;
      margin: 40px auto 12px;
    }
    & > h5 {
      font-size: 18px;
      line-height: 24px;
    }
    & > span {
      font-size: 12.6px;
      line-height: 20px;
      opacity: 0.75;
    }
  }
  .members {
    & > h6 {
      margin-bottom: 16px;
      font-size: 13.6px;
      line-height: 20px;
    }
    & > span {
      color: $font-color-desc;
      font-size: 12px;
    }
    & > .count {
      float: right;
      color: $font-color-content;
      font-size: 14px;
    }
    .users {
      margin: 12px 0;
      .avatar {
        display: inline-block;
        width: 28px;
        height: 28px;
        margin-right: 4px;
      }
    }
  }
}
#__nuxt .workspace-info .el-button {
  width: 100%;
  padding: 12px;
}
</style>
