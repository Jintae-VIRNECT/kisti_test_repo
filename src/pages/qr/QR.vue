<template>
  <section>
    <Header
      v-if="$env !== 'onpremise'"
      :showStatus="statusSet"
      :userInfo="userInfo"
      :env="$env"
      :urls="$urls"
      :subTitle="$t('qrLogin.title')"
      @logout="logout"
    />
    <Header
      v-else
      :showStatus="statusSet"
      :userInfo="userInfo"
      :env="$env"
      :urls="$urls"
      :subTitle="$t('qrLogin.title')"
      :logo="logo"
      @logout="logout"
    />
    <transition name="app-fade" mode="out-in">
      <router-view
        :userInfo="userInfo"
        :customInfo="customInfo"
        :env="$env"
        :subTitle="$t('qrLogin.title')"
      />
    </transition>
    <Footer :urls="$urls" v-if="$env !== 'onpremise'" />
  </section>
</template>

<script>
import { ref, onMounted } from '@vue/composition-api'
import essential from 'service/slice/essential'
import Header from 'components/layout/common/Header.vue'
import Footer from 'components/layout/common/Footer.vue'
export default {
  components: {
    Header,
    Footer
  },
  props: {
    showStatus: Object,
    auth: Object,
  },
  setup(props, { root }) {
    const ESSENTIAL = essential(root)
    const qrImg = ref(null)
    const userInfo = ref(null)
    const statusSet = ref(props.showStatus)

    const logout = async () => {
      try {
        await props.auth.logout()
        location.reload()
      } catch (e) {
        console.error(e)
      }
    }

    const redirection = async () => {
      try {
        if (props.auth.isLogin) {
          userInfo.value = props.auth.myInfo
          statusSet.value.login = !props.auth.isLogin
          statusSet.value.profile = true
          statusSet.value.language = false
        } else throw 'error'
      } catch (e) {
        location.replace(`${root.$urls['console']}/?continue=${location.href}`)
      }
    }

    onMounted(() => {
      redirection()
    })

    return {
      ...ESSENTIAL,
      qrImg,
      userInfo,
      logout,
      statusSet,
    }
  },
}
</script>
