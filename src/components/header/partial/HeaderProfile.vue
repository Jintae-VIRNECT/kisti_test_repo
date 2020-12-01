<template>
  <popover
    placement="bottom-end"
    width="21.429rem"
    trigger="click"
    popperClass="popover-profile"
    :scrollHide="true"
  >
    <profile
      slot="reference"
      :image="account.profile"
      :size="30"
      :thumbStyle="{ width: '36.002px', height: '36.002px' }"
    ></profile>
    <div>
      <div class="popover-profile__myinfo">
        <profile
          :image="account.profile"
          :mainText="account.nickname"
          :subText="account.email"
        ></profile>
      </div>
      <div class="popover-profile__link">
        <button @click="link(urlLink)">
          <span v-if="isOnpremise">Workstation</span>
          <span v-else>VIRNECT Workstation</span>
        </button>
      </div>
      <div class="popover-profile__link">
        <button @click="link(remoteLink)">{{ 'Remote' }}</button>
      </div>
      <div class="popover-profile__link">
        <button @click="logout">{{ $t('button.logout') }}</button>
      </div>
      <div class="popover-profile__version">{{ `web v.${$version}` }}</div>
    </div>
  </popover>
</template>

<script>
import { mapGetters } from 'vuex'
import Popover from 'Popover'
import Profile from 'Profile'
import auth from 'utils/auth'
export default {
  name: 'HeaderProfile',
  components: {
    Popover,
    Profile,
  },
  computed: {
    ...mapGetters(['account']),
    urlLink() {
      return window.urls.workstation
    },
    remoteLink() {
      return window.urls.remote
    },
  },
  methods: {
    link(url) {
      window.open(url)
      this.$nextTick(() => {
        this.$eventBus.$emit('popover:close')
      })
    },
    logout() {
      this.$eventBus.$emit('popover:close')
      this.$nextTick(() => {
        auth.logout()
        // auth.login()
      })
    },
    fileList() {
      this.$eventBus.$emit('popover:close')

      //show media chunk list
      this.$eventBus.$emit('filelist:open')
    },
  },
}
</script>
<style lang="scss">
@import '~assets/style/vars';
@import '~assets/style/mixin';

.popover-profile {
  background-color: #252525;
  border: 1px solid rgb(69, 69, 69);
  border-radius: 8px;
  box-shadow: 0px 2px 4px 0px rgba(11, 31, 72, 0.22);
  transform: translateY(1.571rem);
  > .popover--body {
    padding: 0;
  }
}
.popover-profile__myinfo {
  margin-bottom: 0.429rem;
  padding: 2.143rem;
  border-bottom: solid 1px $color_line_border;
  > .profile .profile--thumb {
    width: 4rem;
    height: 4rem;
  }
  > .profile .profile--text .profile--maintext {
    margin: 0.286rem 0;
    color: #ffffff;
    font-size: 1.214rem;
  }
  > .profile .profile--subtext {
    color: #ffffff;
    font-weight: normal;
    opacity: 0.6;
  }
}
.popover-profile__link {
  margin: 0.143rem 0;
  padding: 0.929rem 2.143rem;
  &:hover {
    background: #424242;
  }

  > button {
    color: #ffffff;
    font-weight: 500;
    font-size: 1.071rem;
    background: transparent;

    // &:active {
    //   font-weight: 500;
    // }
  }
}
.popover-profile__version {
  margin: 0.429rem 0 0.214rem;
  padding: 0.714rem;
  color: #0f75f5;
  font-weight: normal;
  font-size: 0.857rem;
  text-align: center;
  border-top: solid 1px $color_line_border;
}
</style>
