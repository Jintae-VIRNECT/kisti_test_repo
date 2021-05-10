<template>
  <!-- <div class="service-wrapper">Hello World</div> -->
  <router-view :key="key"></router-view>
</template>

<script>
import confirmMixin from 'mixins/confirm'
import auth from 'utils/auth'
import { RUNTIME, RUNTIME_ENV } from 'configs/env.config'

export default {
  name: 'RemoteLayout',
  mixins: [confirmMixin],
  data() {
    return {
      key: 1,
      attr: '',
    }
  },
  computed: {
    onpremise() {
      return RUNTIME.ONPREMISE === RUNTIME_ENV
    },
  },
  methods: {
    viewPointSetting(e) {
      setTimeout(() => {
        this.$eventBus.$emit('popover:close')
        document.documentElement.style.setProperty('overflow', 'auto')
        const metaViewport = document.querySelector('meta[name=viewport]')
        // console.log(e.currentTarget.innerHeight)
        const initialHeight =
          e && e.currentTarget
            ? e.currentTarget.innerHeight
            : window.innerHeight
        metaViewport.setAttribute(
          'content',
          `height=${initialHeight}px,${this.attr}`,
        )
      }, 1000)
    },
  },
  created() {
    this.$eventBus.$on('reJoin', () => {
      this.key++
    })
    if (this.isTablet) {
      let viewport = document.querySelector('meta[name=viewport]')
      this.attr = viewport.getAttribute('content')
      document.body.onorientationchange = this.viewPointSetting
      // window.addEventListener('orientationchange', this.viewPointSetting)
      this.viewPointSetting()
    }

    //구축형 - 강제 로그아웃 이벤트 핸들러 적용 여부
    if (this.onpremise) {
      //마스터에 의해 강제 로그아웃 당할 시
      this.$eventBus.$on('forceLogout', () => {
        //로그아웃 처리
        const action = () => {
          this.$eventBus.$emit('popover:close')
          auth.logout()
        }

        //강제 로그아웃 알림 팝업
        this.confirmDefault(
          this.$t('workspace.confirm_force_logout_received'),
          {
            action,
          },
        )
      })
    }
  },
  beforeDestroy() {
    //이벤트 버스 리스너 청소
    this.$eventBus.$off('reJoin', () => {
      this.key++
    })
    this.$eventBus.$off('forceLogout')
    document.body.onorientationchange = () => {}
  },
}
</script>

<style lang="scss" src="assets/style/layout.scss"></style>
