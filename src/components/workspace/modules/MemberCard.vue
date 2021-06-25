<template>
  <div class="card">
    <popover
      v-if="showMasterMenu"
      class="card-menu"
      popperClass="card-menu__popover"
      placement="bottom-start"
      :scrollHide="true"
    >
      <button
        slot="reference"
        class="card-menu__btn"
        ref="card-menu__btn"
      ></button>
      <ul>
        <li>
          <button
            class="card-menu__group-btn"
            :disabled="isForceLogoutDisabled"
            @click="forceLogout"
          >
            {{ $t('button.logout') }}
          </button>
        </li>
      </ul>
    </popover>
    <div class="card-center">
      <div class="card-profile--thumb">
        <profile
          :image="imageUrl"
          :thumbStyle="{ width: '4.571rem', height: '4.571rem' }"
        ></profile>
        <tooltip
          customClass="status-tooltip"
          :class="status"
          :content="status"
          placement="top"
        >
          <span
            v-if="status"
            slot="body"
            class="card-profile--badge"
            :class="status"
          ></span>
        </tooltip>
      </div>
      <p class="card-profile--name">
        {{ name }}
      </p>
      <p class="card-profile--email">{{ email }}</p>

      <role v-if="showRole" :role="role"></role>
    </div>
    <!-- <div v-if="showMessageButton" class="card-bottom">
      <p>{{ $t('button.send_message') }}</p>
    </div> -->
  </div>
</template>

<script>
import Profile from 'Profile'
import Role from 'Role'
import Tooltip from 'Tooltip'
import Popover from 'Popover'

import { WORKSPACE_ROLE, MEMBER_STATUS } from 'configs/status.config'

export default {
  name: 'Card',
  components: {
    Profile,
    Role,
    Tooltip,
    Popover,
  },

  props: {
    //사용자의 현재 상태
    status: {
      type: String,
      validator: value => ['', ...MEMBER_STATUS].indexOf(value) >= 0,
    },
    imageUrl: String,
    name: {
      type: String,
      default: '',
    },
    email: {
      type: String,
      default: '',
    },
    role: {
      type: String,
      default: '',
    },
    showSignal: {
      type: Boolean,
      default: false,
    },
    showMasterMenu: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {}
  },
  computed: {
    showRole() {
      return (
        this.role === WORKSPACE_ROLE.MASTER ||
        this.role === WORKSPACE_ROLE.MANAGER
      )
    },
    cardWidth() {
      if (this.width === 'full') {
        return '100%'
      } else {
        return this.width + 'px'
      }
    },
    opt() {
      if (this.license) {
        return ''
      } else {
        return 'expired'
      }
    },
    isForceLogoutDisabled() {
      //login 상태인 유저만 강제 로그아웃 가능
      if (this.status !== 'login') return true
      return false
    },
  },
  methods: {
    forceLogout() {
      this.$emit('forceLogout')
    },
    releaseFocus() {
      if (this.$refs['card-menu__btn']) {
        this.$refs['card-menu__btn'].blur()
      }
    },
  },

  /* Lifecycles */
  mounted() {
    //활성화된 popoever 버튼 focus 해제
    this.$eventBus.$on('scroll:memberlist', this.releaseFocus)
  },
  beforeDestroy() {
    this.$eventBus.$off('scroll:memberlist', this.releaseFocus)
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/vars';
@import '~assets/style/mixin';
.card {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 2.286rem;
  padding-bottom: 1.429rem;
  background-color: rgba($color_darkgray_500, 0.9);
  border: solid 1px #3e3e42;
  border-radius: 2px;

  &:hover {
    background-color: $color_darkgray_500;
  }
}

.card-menu {
  position: absolute;
  top: 8px;
  right: 8px;
  .card-menu__btn {
    width: 2em;
    height: 2em;
    background: url(~assets/image/ic_more.svg) 50% no-repeat;
    border-radius: 50%;
    &:active,
    &:focus,
    &:hover {
      background-color: rgba(white, 0.2);
    }
  }
}

.card-center {
  position: relative;
  display: flex;
  flex-direction: column;
  flex-grow: 3;
  align-items: center;
  width: 100%;
  // min-height: 180px;
  margin-bottom: 0.571rem;
  padding-bottom: 1.429rem;
  > .role {
    position: absolute;
    bottom: 0;
    margin: 0;
    color: #f2f2f2;
    font-weight: 300;
    border-color: #ccc;
  }
}

.card-profile--thumb {
  position: relative;
  padding: 0.286rem;

  &.expired {
    @include profileMask(#313135, 1.4px, #c51803);
  }
}

.card-profile--name {
  max-width: 100%;
  margin-top: 1.214rem;
  margin-bottom: 0.286rem;
  padding: 0 1.786rem;
  font-weight: 500;
  font-size: 1.071rem;
  @include ellipsis();
}
.card-profile--email {
  max-width: 100%;
  margin-bottom: 0.786rem;
  padding: 0 1.786rem;
  color: #98a0a6;
  font-size: 0.929rem;
  @include ellipsis();
}

.card-profile--badge {
  width: 24px;
  height: 24px;
  overflow: hidden;
  text-indent: -99px;
  border: 4px solid #313135;
  border-radius: 50%;

  &.join {
    background-color: $color_collabo;
  }
  &.login {
    background-color: $color_login;
  }
  &.logout {
    background-color: $color_logout;
  }
}

.status-tooltip {
  position: absolute;
  right: 0;
  bottom: 0;
}

.card-bottom {
  flex-grow: 1;
  width: 100%;
  margin-bottom: 1rem;
  padding-top: 1rem;
  font-size: 0.929rem;

  text-align: center;
  border-top: solid 1px rgba(#3e3e42, 0.92);
}
</style>
<style lang="scss">
@import '~assets/style/vars';

.status-tooltip .top {
  margin-bottom: 2px;
}

.status-tooltip.login .tooltiptext {
  color: $color_text_login;
}

.status-tooltip.join .tooltiptext {
  color: $color_text_collabo;
}

.status-tooltip.logout .tooltiptext {
  color: $color_text_logout;
}

.status-tooltip .tooltiptext {
  padding: 0.142rem 0.357rem;
  font-weight: 500;
  font-size: 0.857rem;
  text-transform: capitalize;
  background-color: $color_bg;
  border: 1px solid #3d3f40;

  &.top .arrow,
  &.top-start .arrow,
  &.top-end .arrow {
    //꼬리 border 역할
    &:before {
      position: absolute;
      top: 1px;
      left: 0;
      width: 0;
      height: 0;
      margin-top: -7px;
      margin-left: -7px;
      border: solid 7px transparent;
      border-top-color: #3d3f40;
      content: '';
    }
    &:after {
      border-top-color: $color_bg;
    }
  }
}
</style>
