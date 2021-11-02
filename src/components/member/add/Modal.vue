<template>
  <el-dialog
    class="member-add-modal"
    :visible.sync="showMe"
    :title="$t('members.create.new')"
    width="800px"
    :top="cssVars"
    :close-on-click-modal="false"
    :before-close="beforeClose"
  >
    <div class="member-add-modal__content">
      <div class="member-add-modal--left">
        <button
          v-if="!$isOnpremise"
          @click="showTab('invite')"
          class="member-add-modal__tab-button"
          :class="{
            'member-add-modal__tab-button--clicked': tabName === 'invite',
          }"
        >
          <img src="~assets/images/icon/ic-member-account-invitation.svg" />
          <span>{{ $t('members.add.tab.invitation') }}</span>
        </button>
        <button
          v-if="false"
          @click="showTab('create')"
          class="member-add-modal__tab-button"
          :class="{
            'member-add-modal__tab-button--clicked': tabName === 'create',
          }"
        >
          <img src="~assets/images/icon/ic-member-account-creation.svg" />
          <span>{{ $t('members.add.tab.create') }}</span>
        </button>
        <button
          @click="showTab('guest')"
          class="member-add-modal__tab-button"
          :class="{
            'member-add-modal__tab-button--clicked': tabName === 'guest',
          }"
        >
          <img src="~assets/images/icon/ic-member-sheet-registration.svg" />
          <span>{{ $t('members.add.tab.guest') }}</span>
        </button>
      </div>
      <div class="member-add-modal--right">
        <MemberAddInvitePane
          v-show="tabName === 'invite'"
          :membersTotal.sync="membersTotal"
          :maximum.sync="maximum"
          @updated="updated"
        />
        <MemberAddCreatePane
          v-if="false"
          :membersTotal.sync="membersTotal"
          :maximum.sync="maximum"
          @updated="updated"
        />
        <MemberAddGuestPane
          v-show="tabName === 'guest'"
          :membersTotal.sync="membersTotal"
          :maximum.sync="maximum"
          @updated="updated"
        />
      </div>
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'

export default {
  mixins: [modalMixin],
  props: {
    membersTotal: Number,
  },
  data() {
    return {
      showMe: true,
      // TODO: 추후 전용계정 기획이 보충되면, onpremise의 탭이 create가 기본이되도록 설정하기.
      // tabName: this.$isOnpremise ? 'create' : 'invite',
      tabName: this.$isOnpremise ? 'guest' : 'invite',
      maximum: 49,
    }
  },
  methods: {
    showTab(tabName) {
      this.tabName = tabName
    },
    beforeClose(done) {
      this.$emit('close')
      done()
    },
    updated() {
      this.$emit('close')
      this.$emit('refresh')
      this.showMe = false
    },
  },
  computed: {
    cssVars() {
      return this.$isOnpremise ? '4vh' : '11vh'
    },
  },
}
</script>

<style lang="scss">
#__nuxt .member-add-modal {
  .el-dialog__body {
    padding: 0;
    max-height: none;
  }
  &--left {
    border-right: 1px solid #eaeef2;
    padding: 12px;
    width: 184px;
    button:first-child {
      margin-top: 0;
    }
  }
  &--right {
    width: 616px;
  }
  .member-add-modal__content {
    display: flex;
  }
  .member-add-modal__tab-button {
    width: 160px;
    min-height: 38px;
    margin: 2px 0;
    padding: 9px 12px;
    border-radius: 3px;
    text-align: left;
    display: flex;
    &:hover {
      background-color: #eff2f7;
    }
    span {
      @include fontLevel(100);
      margin-left: 9px;
    }
  }
  .member-add-modal__tab-button--clicked {
    @extend .member-add-modal__tab-button;
    background-color: #eff2f7;
  }
}
</style>
