<template>
  <popover
    placement="bottom-end"
    width="21.429rem"
    trigger="click"
    popperClass="popover-profile"
  >
    <profile
      slot="reference"
      :image="account.profile"
      :size="30"
      :thumbStyle="thumbStyle"
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
          {{ $t('common.workstation') }}
        </button>
      </div>
      <div class="popover-profile__link" v-if="!!dashboardLink">
        <button @click="link(dashboardLink)">
          {{ $t('common.dashboard') }}
        </button>
      </div>
      <div class="popover-profile__link" v-if="useLocalRecording && !isSafari">
        <button @click="fileList">{{ $t('common.local_record_file') }}</button>
      </div>
      <div class="popover-profile__link">
        <button @click="logout">{{ $t('button.logout') }}</button>
      </div>
      <div class="popover-profile__version">{{ `web v${$version}` }}</div>
    </div>
  </popover>
</template>

<script>
import { mapGetters } from 'vuex'
import Popover from 'Popover'
import Profile from 'Profile'
import auth from 'utils/auth'
import { URLS } from 'configs/env.config'
import thumbStyle from 'mixins/thumbStyle'

const DEFAULT_THUMBSTYLE_SIZE = '2.143rem'
const MOBILE_THUMBSTYLE_SIZE = '3.0rem'

export default {
  name: 'HeaderProfile',
  components: {
    Popover,
    Profile,
  },
  mixins: [thumbStyle],
  data() {
    return {
      thumbStyle: {
        width: DEFAULT_THUMBSTYLE_SIZE,
        height: DEFAULT_THUMBSTYLE_SIZE,
      },
    }
  },
  computed: {
    ...mapGetters(['account', 'useStorage', 'useLocalRecording']),
    urlLink() {
      return URLS['workstation']
    },
    dashboardLink() {
      if (URLS['dashboard']) {
        return URLS['dashboard']
      } else {
        return false
      }
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
    async downloadManual() {
      if (!this.isOnpremise) {
        window.open('https://file.virnect.com/Guide/remote_web_user_guide.pdf')
      } else {
        window.open(`${window.urls.minio}/guide/remote_web_user_guide.pdf`)
      }
    },
  },

  /* Lifecycles */
  mounted() {
    this.setSizeVariable(
      DEFAULT_THUMBSTYLE_SIZE,
      DEFAULT_THUMBSTYLE_SIZE,
      MOBILE_THUMBSTYLE_SIZE,
      MOBILE_THUMBSTYLE_SIZE,
    )
    this.activateThumbStyleHandlerOnMobileSize()
  },
}
</script>
<style lang="scss">
@import '~assets/style/vars';
@import '~assets/style/mixin';

.popover-profile {
  background-color: $color_bg_sub;
  border: solid 1px rgba($color_sub_border, 0.12);
  border-radius: 8px;
  transform: translateY(1.571rem);
  > .popover--body {
    padding: 0;
  }
}
.popover-profile__myinfo {
  margin-bottom: 0.429rem;
  padding: 2.143rem;
  border-bottom: solid 1px rgba($color_line_border, 0.06);
  > .profile .profile--thumb {
    width: 4rem;
    height: 4rem;
  }
  > .profile .profile--text .profile--maintext {
    margin: 0.286rem 0;
    font-size: 1.214rem;
  }
}
.popover-profile__link {
  margin: 0.143rem 0;
  padding: 0.929rem 2.143rem;
  > button {
    color: $color_text;
    font-weight: 300;
    font-size: 1.071rem;
    background: transparent;
    &:hover,
    &:active {
      font-weight: 500;
    }
  }
}
.popover-profile__version {
  margin: 0.429rem 0 0.214rem;
  padding: 0.714rem;
  color: $color_primary_400;
  font-size: 0.857rem;
  text-align: center;
  border-top: solid 1px rgba($color_line_border, 0.06);
}
</style>
