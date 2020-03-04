<template lang="pug">
  .top-nav
    .top-nav__left
      router-link(to="/")
        img.logo-img(src="~@/assets/image/top-bar-logo-smart-factory.png") 
      .divider
      label.workspace {{getWorkspaceName}}
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

<style lang="scss" scoped>
.el-dropdown {
  cursor: pointer;
}
</style>
