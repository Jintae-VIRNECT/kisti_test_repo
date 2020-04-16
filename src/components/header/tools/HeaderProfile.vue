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
        <button>VIRNECT Workstation</button>
      </div>
      <div class="popover-profile__link">
        <button>QR 로그인</button>
      </div>
      <div class="popover-profile__link">
        <button>Open Source License</button>
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
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
<style lang="scss" scoped>
.header-tools__profile.profile-image {
  margin: 2px 2px 2px 13px;
  cursor: pointer;
  &:hover {
    opacity: 0.8;
  }
}
</style>
<style lang="scss">
.popover-profile {
  background-color: #242427;
  border: solid 1px rgba(#979797, 0.12);
  border-radius: 8px;
  transform: translateY(22px);
  > .popover--body {
    padding: 0;
  }
}
.popover-profile__myinfo {
  margin-bottom: 6px;
  padding: 30px;
  border-bottom: solid 1px rgba(#eaeeee, 0.06);
  > .profile .profile--thumb {
    width: 56px;
    height: 56px;
  }
  > .profile .profile--text .profile--maintext {
    margin: 7px 0 4px;
    font-size: 17px;
  }
}
.popover-profile__link {
  margin: 2px 0;
  padding: 13px 30px;
  > button {
    color: #fff;
    font-weight: 500;
    font-size: 15px;
    background: transparent;
  }
}
.popover-profile__version {
  margin: 6px 0 3px;
  padding: 10px;
  color: #bfddff;
  font-size: 12px;
  text-align: center;
  border-top: solid 1px rgba(#eaeeee, 0.06);
}
</style>
