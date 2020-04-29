<template>
  <popover
    placement="bottom-end"
    :width="300"
    trigger="click"
    popperClass="popover-profile"
  >
    <profile-image
      slot="reference"
      :image="account.profile"
      :size="30"
      customClass="header-tools__profile"
    ></profile-image>
    <div>
      <div class="popover-profile__myinfo">
        <profile
          :image="account.profile"
          :mainText="account.name"
          :subText="account.email"
        ></profile>
      </div>
      <div class="popover-profile__link">
        <button @click="link('https://virnect.com')">
          VIRNECT Workstation
        </button>
      </div>
      <div class="popover-profile__link">
        <button @click="logout">로그아웃</button>
      </div>
      <div class="popover-profile__version">web v.2.0</div>
    </div>
  </popover>
</template>

<script>
import { mapGetters } from 'vuex'
import Popover from 'Popover'
import Profile from 'Profile'
import ProfileImage from 'ProfileImage'
// import auth from 'utils/auth'
export default {
  name: 'HeaderProfile',
  components: {
    Popover,
    Profile,
    ProfileImage,
  },
  computed: {
    ...mapGetters(['account']),
  },
  methods: {
    link(url) {
      this.$eventBus.$emit('popover:close')
      this.$nextTick(() => {
        window.open(url)
      })
    },
    logout() {
      this.$eventBus.$emit('popover:close')
      this.$nextTick(() => {
        // auth.logout()
        // auth.login()
      })
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
<style lang="scss" scoped>
.header-tools__profile.profile-image {
  margin: 0.143rem 0.143rem 0.143rem 0.929rem;
  cursor: pointer;
  &:hover {
    opacity: 0.8;
  }
}
</style>
<style lang="scss">
@import '~assets/style/vars';

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
    margin: 0.5rem 0 0.286rem;
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
  color: #bfddff;
  font-size: 0.857rem;
  text-align: center;
  border-top: solid 1px rgba($color_line_border, 0.06);
}
</style>
