<template>
  <div class="card">
    <popover
      v-if="showMasterMenu"
      class="card-menu"
      popperClass="card-menu__popover"
      placement="bottom-start"
    >
      <button slot="reference" class="card-menu__btn"></button>
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
        <span
          v-if="status && license"
          class="card-profile--badge"
          :class="status"
          >{{ status }}</span
        >
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
import Popover from 'Popover'
import { WORKSPACE_ROLE } from 'configs/status.config'
export default {
  name: 'Card',
  components: {
    Profile,
    Role,
    Popover,
  },

  props: {
    status: {
      type: String,
      validator: value => ['', 'online', 'busy', 'offline'].indexOf(value) >= 0,
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
      if (this.status !== 'login') return true
      return false
    },
  },
  methods: {
    forceLogout() {
      this.$emit('forceLogout')
    },
  },

  /* Lifecycles */
  mounted() {},
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
  position: absolute;
  right: 0;
  bottom: 0;
  width: 26%;
  height: 26%;
  overflow: hidden;
  text-indent: -99px;
  border-radius: 50%;

  &.busy {
    background-color: $color_busy;
  }
  &.online {
    background-color: $color_online;
  }
  &.offline {
    background-color: $color_offline;
  }
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
