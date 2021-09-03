<template>
  <el-dialog
    class="member-setting-modal"
    :visible.sync="showMe"
    :title="$t('members.setting.title')"
    width="680px"
    top="11vh"
    :close-on-click-modal="false"
  >
    <el-row type="flex">
      <el-col :span="7" class="member-setting-modal--left">
        <button
          @click="showTab('account')"
          class="member-setting-modal__tab-button"
          :class="{
            'member-setting-modal__tab-button--clicked': tabName === 'account',
          }"
        >
          {{ $t('members.setting.account') }}
        </button>
        <button
          @click="showTab('plan')"
          class="member-setting-modal__tab-button"
          :class="{
            'member-setting-modal__tab-button--clicked': tabName === 'plan',
          }"
        >
          {{ $t('members.setting.givePlans') }}
        </button>
        <button
          @click="showTab('role')"
          class="member-setting-modal__tab-button"
          :class="{
            'member-setting-modal__tab-button--clicked': tabName === 'role',
          }"
        >
          {{ $t('members.setting.workspaceRole') }}
        </button>
      </el-col>
      <el-col :span="17" class="member-setting-modal--right">
        <MemberSettingAccountPane
          v-show="tabName === 'account'"
          :showModal="showMe"
          :member="form"
          :profile.sync="data.profile"
          :nickname.sync="data.nickname"
          @deleteSeat="$emit('deleteSeat')"
        />
        <MemberSettingPlanPane
          v-show="tabName === 'plan'"
          :member="form"
          :remote="plansInfo.remote.unUsedAmount"
          :make="plansInfo.make.unUsedAmount"
          :view="plansInfo.view.unUsedAmount"
          :originPlan="originPlan"
          @update="memberInfoUpdate"
        />
        <MemberSettingRolePane
          v-show="tabName === 'role'"
          :member="form"
          @update="memberInfoUpdate"
          @kick="$emit(kick)"
        />
      </el-col>
    </el-row>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
import filterMixin from '@/mixins/filters'
import { mapGetters } from 'vuex'
import EditMember from '@/models/workspace/EditMember'

export default {
  mixins: [filterMixin, modalMixin],
  props: {
    data: Object,
  },
  data() {
    return {
      form: {},
      tabName: 'account',
    }
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
      plansInfo: 'plan/plansInfo',
      auth: 'auth/auth',
    }),
  },
  methods: {
    opened() {
      this.form = new EditMember(this.data)
      this.originPlan = {
        licenseRemote: this.form.licenseRemote,
        licenseMake: this.form.licenseMake,
        licenseView: this.form.licenseView,
      }
      if (!this.plansInfo.planStatus) {
        this.$store.dispatch('plan/getPlansInfo')
      }
    },
    showTab(tabName) {
      this.tabName = tabName
    },
    memberInfoUpdate(info) {
      this.$emit('updated', info)
    },
  },
  created() {
    // 컴포넌트가 생성될 때 실행
    this.opened()
  },
}
</script>

<style lang="scss">
#__nuxt .member-setting-modal {
  .el-dialog__body {
    overflow-y: scroll;
    padding: 0;
    .el-row--flex {
      min-height: 487px;
    }
  }
  .el-form-item__content {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    .el-input {
      width: 320px;
      margin-right: 8px;
    }
  }
  .el-form-item__label:first-child {
    padding-bottom: 4px;
  }
  .el-form-item__label {
    padding: 0;
  }

  h6 {
    @include fontLevel(100);
    color: #0b1f48;
    margin-bottom: 16px;
  }
  &--left {
    border-right: 1px solid #eaeef2;
    padding: 12px;
    button:first-child {
      margin-top: 0;
    }
  }
  &--right {
    padding: 24px;
  }
  .member-setting-modal__tab-button {
    width: 160px;
    height: 38px;
    margin: 2px 0;
    padding: 9px 12px;
    border-radius: 3px;
    text-align: left;
    &:hover {
      background-color: #eff2f7;
    }
  }
  .member-setting-modal__tab-button--clicked {
    @extend .member-setting-modal__tab-button;
    background-color: #eff2f7;
  }
}
</style>
