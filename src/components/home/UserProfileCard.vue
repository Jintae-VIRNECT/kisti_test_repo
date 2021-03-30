<template>
  <el-card class="user-profile-card">
    <div slot="header">
      <h3>
        <span>{{ $t('home.profile.title') }}</span>
      </h3>
      <a :href="`${$url.account}/profile`" target="_blank">
        {{ $t('home.profile.link') }}
      </a>
    </div>
    <dl>
      <dt>{{ $t('home.profile.usingNickname') }}</dt>
      <dd>
        <div class="avatar">
          <VirnectThumbnail :size="40" :image="cdn(profile.profile)" />
        </div>
        <span class="name">{{ profile.nickname }}</span>
        <span class="email">{{ profile.email }}</span>
      </dd>
      <dt>{{ $t('home.profile.workspaceRole') }}</dt>
      <dd class="column-role">
        <el-tag :class="profile.role">{{ profile.role }}</el-tag>
      </dd>
      <dt>{{ $t('home.profile.usingPlans') }}</dt>
      <dd class="plans">
        <div class="plan" v-for="plan in profile.licenseProducts" :key="plan">
          <img :src="plans[plan].logo" />
          <span>{{ plans[plan].label }}</span>
        </div>
      </dd>
    </dl>
  </el-card>
</template>

<script>
import { mapGetters } from 'vuex'
import workspaceService from '@/services/workspace'
import plans from '@/models/workspace/plans'
import filterMixin from '@/mixins/filters'

export default {
  mixins: [filterMixin],
  data() {
    return {
      profile: {},
      plans: Object.values(plans).reduce((o, n) => {
        o[n.value] = n
        return o
      }, {}),
    }
  },
  computed: {
    ...mapGetters({
      myProfile: 'auth/myProfile',
    }),
  },
  methods: {
    async getMyInfo() {
      this.profile = await workspaceService.getMemberInfo(this.myProfile.uuid)
    },
  },
  beforeMount() {
    this.getMyInfo()
    workspaceService.watchActiveWorkspace(this, this.getMyInfo)
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
    margin-right: 12px;

    .thumbnail {
      height: 67%;
    }
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
  .plans {
    margin-bottom: 8px;
  }
}
</style>
