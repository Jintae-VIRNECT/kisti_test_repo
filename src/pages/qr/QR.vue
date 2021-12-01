<template>
  <section>
    <VirnectHeader
      v-if="$env !== 'onpremise'"
      :showStatus="statusSet"
      :userInfo="userInfo"
      :env="$env"
      :urls="$urls"
      :subTitle="$t('qrLogin.title')"
      @logout="logout"
    />
    <VirnectHeader
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
    <VirnectFooter :urls="$urls" v-if="$env !== 'onpremise'" />
  </section>
</template>

<script>
import { ref, onMounted } from '@vue/composition-api'
import essential from 'service/slice/essential'
export default {
  props: {
    showStatus: Object,
    auth: Object,
  },
  setup(props, { root }) {
    const ESSENTIAL = essential(root)
    const qrImg = ref(null)
    const userInfo = ref(null)

    const logout = async () => {
      try {
        await props.auth.logout()
        location.reload()
      } catch (e) {
        console.error(e)
      }
    }

    const statusSet = ref(props.showStatus)

    const redirection = async () => {
      try {
        if (props.auth.isLogin) {
          console.log(props.showStatus)
          userInfo.value = props.auth.myInfo
          statusSet.value.login = !props.auth.isLogin
          statusSet.value.profile = true
          statusSet.value.language = false
        } else throw 'error'
      } catch (e) {
        // props.showStatus.profile = false
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
