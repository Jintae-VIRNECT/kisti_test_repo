<template lang="pug">
  .top-nav
    .top-nav__left
      router-link(to="/")
        img.logo-img(src="~@/assets/image/top-bar-logo-smart-factory.png") 
      .divider
      label.workspace Workstation
    .top-nav__right(v-if="$store.getters.getIsLoggedIn === true")
      img.profile-img(:src="getUser.profile") 
      el-dropdown(trigger="click")
        span.username {{getUser.name}}
        el-dropdown-menu(slot='dropdown')
          el-dropdown-item
            div(@click="openQRcodeModal") 로그인용 QR
      button.logout-btn(@click="userLogout") 로그아웃
      QRCodeModal(:toggleQRModal="qrModal" @handleCancel="qrHandleCancel")
</template>

<script>
import { mapGetters } from 'vuex'
import QRCodeModal from '@/components/home/QRCodeModal'

export default {
  components: {
    QRCodeModal,
  },
  data() {
    return {
      activeLink: null,
      qrModal: false,
    }
  },
  computed: {
    ...mapGetters(['getUser', 'getLocale', 'getWorkspaceName']),
  },
  mounted() {
    this.activeLink = this.$route.path
  },
  methods: {
    async userLogout() {
      try {
        await this.$store.dispatch('USER_LOGOUT', {
          user: this.$store.uid,
        })
        this.$router.go('/')
      } catch (e) {
        console.log('e : ', e)
      }
    },
    openQRcodeModal() {
      this.qrModal = true
    },
    qrHandleCancel() {
      this.qrModal = false
    },
  },
  watch: {
    $route(to) {
      const paths = to.path.split('/')
      this.activeLink = '/' + paths[1]
    },
  },
}
</script>

<style lang="scss">
#app {
  .top-nav {
    position: fixed;
    z-index: 10;
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: calc(100% - 76px);
    min-width: 600px;
    padding: 12px 22px;
    background: #fafbfc;
    // box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.04);

    .el-dropdown > span {
      cursor: pointer;
    }
  }
  .top-nav__center,
  .top-nav__left,
  .top-nav__right {
    position: static;
    height: auto;
    padding: 0;
    .username {
      font-size: 14px;
    }
  }
  .top-nav__center {
    margin: 0 auto;
  }
  .top-nav__right {
    padding-right: 8px;
  }
}

// static modal 버그
.v-modal {
  display: none;
}
.el-dialog__wrapper {
  height: 110%;
  background: rgba(0, 0, 0, 0.5);
}

// 임시 모바일 스타일
@media screen and (max-width: 767px) {
  .top-nav__left {
    display: none;
  }
}
</style>
