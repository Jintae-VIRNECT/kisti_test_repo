<template>
  <!-- <div class="service-wrapper">Hello World</div> -->
  <router-view></router-view>
</template>

<script>
import Auth from 'utils/auth'
import confirmMixin from 'mixins/confirm'
const betaIntervalTime = 60 * 60 * 1000 // 60 minutes
// const betaIntervalTime = 15 * 1 * 1000 // 15 seconds
export default {
  name: 'RemoteLayout',
  mixins: [confirmMixin],
  data() {
    return {
      betaInterval: null,
    }
  },
  computed: {},
  watch: {},
  methods: {
    startInterval() {
      this.betaInterval = setInterval(this.logout, betaIntervalTime)
    },
    logout() {
      this.$call.leave()
      this.$router.push({ name: 'workspace' })
      this.$nextTick(() => {
        this.confirmDefault(
          `접속을 종료합니다.​
          서비스를 이용하시려면 다시 실행해 주세요`,
          { text: '종료', action: Auth.logout },
        )
      })
    },
    init() {
      if (!this.betaInterval) {
        this.confirmDefault(
          `베타 서비스를 진행 중 입니다.​
        서비스를 진행하는 동안 접속 안정성이 낮을 수 있습니다.​
        서비스 시간은 로그인 이후에 1시간 동안만 접속을 유지합니다.​`,
          {
            text: '확인',
          },
          { allowOutsideClick: false },
        )
        this.startInterval()
      }
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/layout.scss"></style>
