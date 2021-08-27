<template>
  <popover
    placement="bottom-end"
    :width="width"
    trigger="click"
    popperClass="popover-profile"
    @visible="setVisible"
  >
    <profile
      :class="{ visible }"
      slot="reference"
      :profileMenu="true"
      :image="account.profile"
      :size="30"
      :thumbStyle="thumbStyle"
    ></profile>
    <div>
      <div class="popover-profile__tools">
        <stream size="4rem"></stream>
        <mic size="4rem"></mic>
        <speaker size="4rem"></speaker>
      </div>
      <div class="popover-profile__myinfo">
        <profile
          :image="account.profile"
          :mainText="account.nickname"
          :subText="account.email"
          :thumbStyle="myInfothumbStyle"
        ></profile>
      </div>
      <div class="popover-profile__linklist">
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
        <div
          class="popover-profile__link"
          v-if="useLocalRecording && !isSafari"
        >
          <button @click="fileList">
            {{ $t('common.local_record_file') }}
          </button>
        </div>
        <div class="popover-profile__link">
          <button @click="logout">{{ $t('button.logout') }}</button>
        </div>
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
import Stream from '../tools/Stream'
import Mic from '../tools/Mic'
import Speaker from '../tools/Speaker'

const defaultPopoverWidth = '21.429rem'
const mobilePopoeverWidth = '26.5rem'
const DEFAULT_THUMBSTYLE_SIZE = { width: '2.143rem', height: '2.143rem' }
const MOBILE_THUMBSTYLE_SIZE = { width: '4.0rem', height: '4.0rem' }
const DEFAULT_MYINFO_THUMBSTYLE_SIZE = { width: '5.143rem', height: '5.143rem' }
const MOBILE_MYINFO_THUMBSTYLE_SIZE = { width: '4.2rem', height: '4.2rem' }

export default {
  name: 'HeaderProfile',
  components: {
    Popover,
    Profile,
    Stream,
    Mic,
    Speaker,
  },
  data() {
    return {
      thumbStyle: {
        width: DEFAULT_THUMBSTYLE_SIZE,
        height: DEFAULT_THUMBSTYLE_SIZE,
      },
      myInfothumbStyle: {
        width: DEFAULT_MYINFO_THUMBSTYLE_SIZE,
        height: DEFAULT_MYINFO_THUMBSTYLE_SIZE,
      },
      visible: false,
      width: defaultPopoverWidth,
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
  watch: {
    isMobileSize: {
      immediate: true,
      handler: function(newVal) {
        if (newVal) this.setResponsiveMobile()
        else this.setResponsiveDefault()
      },
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
    setVisible(visible) {
      this.visible = visible
    },
    setResponsiveMobile() {
      this.width = mobilePopoeverWidth
      this.thumbStyle = MOBILE_THUMBSTYLE_SIZE
      this.myInfothumbStyle = MOBILE_MYINFO_THUMBSTYLE_SIZE
    },
    setResponsiveDefault() {
      this.width = defaultPopoverWidth
      this.thumbStyle = DEFAULT_THUMBSTYLE_SIZE
      this.myInfothumbStyle = DEFAULT_MYINFO_THUMBSTYLE_SIZE
    },
  },

  /* Lifecycles */
  mounted() {},
  beforeDestroy() {},
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
.popover-profile__tools {
  display: none;
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

@include responsive-mobile {
  .popover.popover-profile {
    background-color: $new_color_bg_popover;
    border: none;
  }
  .popover-profile__tools {
    display: flex;
    align-items: center;
    height: 6.9rem;
    padding-left: 2rem;
    border-bottom: 1.5px solid $new_color_line_border;

    .toggle-button.toggle-header {
      margin-right: 1.6rem;
      background-color: $new_color_bg_icon;
      background-size: 110%;
      border-radius: 0.9rem;
    }
  }
  .popover-profile__myinfo {
    display: flex;
    align-items: center;
    height: 7.45rem;
    padding: 0;
    padding-left: 2rem;
    border-bottom: 1.5px solid $new_color_line_border;

    .profile {
      align-items: center;
      .profile--text > .profile--maintext {
        margin: 0;
        color: $new_color_text_main;
        @include fontLevel(100);
      }
      .profile--text > .profile--subtext {
        color: $new_color_text_sub;
        @include fontLevel(75);
      }
    }
  }
  .popover-profile__linklist {
    padding: 0.6rem 0;
  }
  .popover-profile__link {
    padding-top: 0.7rem;
    padding-bottom: 0.7rem;
    > button {
      @include fontLevel(100);
      color: $new_color_text_main;
    }
  }
  .popover-profile__version {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 4rem;
    color: $new_color_text_blue;
    border-top: 1.5px solid $new_color_line_border;
    @include fontLevel(50);
  }
}
</style>
