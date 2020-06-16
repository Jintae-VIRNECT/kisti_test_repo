<template>
  <nav class="header-lnb service">
    <ul class="flex">
      <lnb-button
        text="실시간 공유"
        keyvalue="stream"
        :image="require('assets/image/call/gnb_ic_shareframe.svg')"
        @click="setView('stream')"
      ></lnb-button>
      <lnb-button
        text="협업 보드"
        keyvalue="drawing"
        :image="require('assets/image/call/gnb_ic_creat_basic.svg')"
        @click="setView('drawing')"
      ></lnb-button>
      <lnb-button
        text="AR 기능"
        keyvalue="ar"
        :notice="true"
        :image="require('assets/image/call/gnb_ic_creat_ar.svg')"
        @click="permissionCheck"
      ></lnb-button>
    </ul>
  </nav>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
import { SIGNAL } from 'configs/remote.config'
import LnbButton from '../tools/LnbButton'
export default {
  name: 'HeaderServiceLnb',
  components: {
    LnbButton,
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['mainView', 'participants']),
  },
  methods: {
    ...mapActions(['setView']),
    permissionCheck() {
      if (this.mainView.id === this.account.uuid) {
        console.error('본인 영상입니다.')
        return
      }
      if (this.mainView.permission === true) {
        this.setView('ar')
        return
      }

      this.$call.permission({
        to: this.mainView.id,
      })
    },
    getPermissionCheck(receive) {
      const data = JSON.parse(receive.data)

      // 내가 요청한 허용인지 체크
      if (data.to !== this.account.uuid) return

      // 웹-웹 테스트용!!!
      if (!('value' in data)) {
        this.$call.permission({
          to: data.from,
          value: true,
        })
      }

      // 허용값 체크
      if (!data.value) return

      const idx = this.participants.findIndex(user => user.id === data.from)
      if (idx < 0) return

      this.$store.commit('agreePermission', data.from)
      this.setView('ar')
    },
  },

  /* Lifecycles */
  created() {
    this.$call.addListener(SIGNAL.CAPTURE_PERMISSION, this.getPermissionCheck)
  },
}
</script>
