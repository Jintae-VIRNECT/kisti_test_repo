<template>
  <el-card class="member-profile-card">
    <dl>
      <dt>
        <span>{{ $t('members.card.profile') }}</span>
        <span
          v-if="canSettingMemberInfo"
          class="dropdown"
          @click="openMemberSettingModal"
        >
          <img src="~assets/images/icon/ic-more-horiz.svg" />
        </span>
      </dt>
      <dd>
        <div class="avatar">
          <div
            class="image"
            :style="`background-image: url(${data.profile})`"
          />
        </div>
        <div class="column-role">
          <el-tag :class="data.role">{{ data.role }}</el-tag>
        </div>
        <span class="name">{{ data.nickname }}</span>
        <span class="email">{{ data.email }}</span>
      </dd>
      <el-divider />
      <dt>{{ $t('members.card.usingPlans') }}</dt>
      <dd class="plans">
        <div class="plan" v-for="plan in data.licenseProducts" :key="plan">
          <img :src="plans[plan].logo" />
          <span>{{ plans[plan].label }}</span>
        </div>
      </dd>
      <dt>{{ $t('members.card.links') }}</dt>
      <dd class="column-links">
        <router-link to="/contents">
          <img src="~assets/images/icon/ic-contents.svg" />
          <span>{{ $t('members.card.contents') }}</span>
        </router-link>
        <router-link to="/tasks">
          <img src="~assets/images/icon/ic-work.svg" />
          <span>{{ $t('members.card.work') }}</span>
        </router-link>
        <router-link to="/tasks/results">
          <img src="~assets/images/icon/ic-report.svg" />
          <span>{{ $t('members.card.report') }}</span>
        </router-link>
      </dd>
    </dl>
    <member-setting-modal
      :data="myInfo"
      :visible.sync="showMemberSettingModal"
      @updated="updated"
      @kick="kick"
    />
    <member-kick-modal
      :data="myInfo"
      :visible.sync="showMemberKickModal"
      @back="back"
      @kicked="kicked"
    />
  </el-card>
</template>

<script>
import MemberSettingModal from '@/components/member/MemberSettingModal'
import MemberKickModal from '@/components/member/MemberKickModal'
import { mapGetters } from 'vuex'
import plans from '@/models/workspace/plans'

export default {
  components: {
    MemberSettingModal,
    MemberKickModal,
  },
  props: {
    data: Object,
  },
  data() {
    return {
      myInfo: this.data,
      showMemberSettingModal: false,
      showMemberKickModal: false,
      plans: Object.values(plans).reduce((o, n) => {
        o[n.value] = n
        return o
      }, {}),
    }
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'workspace/activeWorkspace',
    }),
    canSettingMemberInfo() {
      return this.activeWorkspace.role !== 'MEMBER'
    },
  },
  methods: {
    openMemberSettingModal() {
      this.showMemberSettingModal = true
    },
    updated(form) {
      this.myInfo.role = form.role
      this.showMemberSettingModal = false
    },
    kicked() {
      this.showMemberKickModal = false
      this.showMemberSettingModal = false
    },
    kick() {
      this.showMemberKickModal = true
      // this.showMemberSettingModal = false
    },
    back() {
      this.showMemberSettingModal = true
      this.showMemberKickModal = false
    },
  },
}
</script>

<style lang="scss">
.member-profile-card {
  height: 336px;
}
.member-profile-card .el-card__body > dl {
  dt {
    margin-bottom: 8px;
    color: $font-color-desc;
    font-size: 12px;

    &:first-child {
      margin-bottom: 12px;
    }
  }
  dd {
    margin-bottom: 12px;
  }
  .dropdown {
    float: right;
    cursor: pointer;
  }
  .avatar {
    float: right;
    width: 70px;
    height: 70px;
    margin-left: 10px;
  }
  .name {
    display: block;
    margin: 24px 0 4px;
    overflow: hidden;
    font-size: 20px;
    line-height: 24px;
    white-space: nowrap;
    text-overflow: ellipsis;
    word-break: break-all;
  }
  .email {
    display: block;
    color: $font-color-desc;
    font-size: 12.6px;
    line-height: 20px;
  }
  .el-divider {
    margin: 24px 0 12px;
  }
  .plans {
    height: 32px;
  }
}
</style>
