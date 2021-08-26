<template>
  <section class="remote-layout">
    <header-section></header-section>
    <div class="workspace-wrapper">
      <workspace-welcome
        ref="welcomeSection"
        :inited="inited"
      ></workspace-welcome>
    </div>
  </section>
</template>

<script>
import HeaderSection from 'components/header/Header'

import confirmMixin from 'mixins/confirm'
import langMixin from 'mixins/language'
import toastMixin from 'mixins/toast'

import errorMsgMixin from 'mixins/errorMsg'
import { MyStorage } from 'utils/storage'

import WorkspaceWelcome from '../workspace/section/WorkspaceWelcome.vue'

export default {
  name: 'GuestLayout',
  // async beforeRouteEnter(to, from, next) {
  //   console.log(to)
  //   console.log(from)
  //   console.log(next)
  //   next()
  // },
  mixins: [confirmMixin, langMixin, toastMixin, errorMsgMixin],
  components: {
    HeaderSection,
    WorkspaceWelcome,
  },
  data() {
    return {
      url: '',
    }
  },

  async mounted() {
    //@TODO:seat member의 uuid 넣기
    window.myStorage = new MyStorage('testuserid')
    console.log(this.$route.query)
  },
  beforeDestroy() {},
}
</script>

<style lang="scss" src="assets/style/guest.scss"></style>
