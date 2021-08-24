<template>
  <el-card class="member-profile-card">
    <dl>
      <dt>
        <span>{{ $t('members.card.profile') }}</span>
        <span class="dropdown" @click="openMemberSettingModal">
          <img src="~assets/images/icon/ic-more-horiz.svg" />
        </span>
      </dt>
      <dd>
        <VirnectThumbnail :size="70" :image="cdn(myInfo.profile)" />
        <div class="column-role">
          <el-tag :class="myInfo.role">{{ myInfo.role }}</el-tag>
        </div>
        <span class="name">{{ myInfo.nickname }}</span>
        <span class="email" v-if="myInfo.role === 'SEAT'">{{
          myInfo.uuid
        }}</span>
        <span class="email" v-else>{{ myInfo.email }}</span>
      </dd>
      <el-divider />
      <dt>{{ $t('members.card.usingPlans') }}</dt>
      <dd class="plans">
        <div class="plan" v-for="plan in myInfo.licenseProducts" :key="plan">
          <img :src="plans[plan].logo" />
          <span>{{ plans[plan].label }}</span>
        </div>
      </dd>
      <dt>{{ $t('members.card.links') }}</dt>
      <dd class="column-links">
        <router-link :to="`/contents?search=${myInfo.email}`">
          <img src="~assets/images/icon/ic-contents.svg" />
          <span>{{ $t('members.card.contents') }}</span>
        </router-link>
        <router-link :to="`/tasks?search=${myInfo.email}`">
          <img src="~assets/images/icon/ic-work.svg" />
          <span>{{ $t('members.card.work') }}</span>
        </router-link>
        <router-link :to="`/tasks/results/papers?search=${myInfo.email}`">
          <img src="~assets/images/icon/ic-report.svg" />
          <span>{{ $t('members.card.paper') }}</span>
        </router-link>
      </dd>
    </dl>
    <OnpremiseMemberSettingModal
      :data="myInfo"
      :visible.sync="showMemberSettingModal"
      v-if="$isOnpremise"
      @updated="updated"
      @kick="kick"
      @delete="deleteAccount"
      @change-password="showMemberPasswordModal = true"
    />
    <MemberSettingModal
      :data="myInfo"
      :visible.sync="showMemberSettingModal"
      v-else
      @updated="updated"
      @deleteSeat="deleteSeat"
      @delete="deleteAccount"
      @change-password="showMemberPasswordModal = true"
    />
    <MemberKickModal
      v-if="!$isOnpremise"
      :data="myInfo"
      :visible.sync="showMemberKickModal"
      @back="back"
      @kicked="kicked"
    />

    <OnpremiseMemberPasswordModal
      v-if="$isOnpremise"
      :data="myInfo"
      :visible.sync="showMemberPasswordModal"
    />
    <OnpremiseMemberDeleteModal
      :data="myInfo"
      :visible.sync="showMemberDeleteModal"
      @kicked="kicked"
    />
  </el-card>
</template>

<script>
import { mapGetters } from 'vuex'
import plans from '@/models/workspace/plans'
import filterMixin from '@/mixins/filters'

export default {
  mixins: [filterMixin],
  props: {
    data: Object,
  },
  data() {
    return {
      myInfo: this.data,
      showMemberSettingModal: false,
      showMemberKickModal: false,
      showMemberPasswordModal: false,
      showMemberDeleteModal: false,
      plans,
    }
  },
  watch: {
    data(val) {
      this.myInfo = val
    },
  },
  mounted() {
    console.log(this.myInfo)
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
    }),
  },
  methods: {
    openMemberSettingModal() {
      this.showMemberSettingModal = true
    },
    updated(form) {
      this.myInfo.role = form.role
      this.myInfo.licenseProducts = [
        form.licenseRemote && plans.remote.value,
        form.licenseMake && plans.make.value,
        form.licenseView && plans.view.value,
      ].filter(_ => _)
      this.showMemberSettingModal = false
      this.$emit('refresh')
    },
    kicked() {
      this.showMemberKickModal = false
      this.showMemberDeleteModal = false
      this.showMemberSettingModal = false
      this.$emit('refresh')
    },
    kick() {
      if (this.$isOnpremise) {
        this.showMemberDeleteModal = true
      } else {
        this.showMemberKickModal = true
      }
    },
    deleteSeat() {
      this.showMemberDeleteModal = false
      this.showMemberSettingModal = false
      this.$emit('refresh')
    },
    deleteAccount() {
      this.showMemberDeleteModal = true
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
  min-height: 347px;
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
  .virnect-thumbnail {
    float: right;
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
    font-size: 13px;
    line-height: 20px;
  }
  .el-divider {
    margin: 24px 0 12px;
  }
  .plans {
    min-height: 32px;
  }
}
</style>
