import { setQueueAct } from 'plugins/remote/call/RemoteSessionEventListener'
import { mapGetters } from 'vuex'
import { SIGNAL } from 'configs/remote.config'

export default {
  computed: {
    ...mapGetters(['fileShareEventQueue']),
  },
  watch: {
    //해당 component 생성 전에 발생했던 이벤트를 vuex에 저장해두고 처리한다.
    fileShareEventQueue: {
      immediate: true,
      handler(newVal) {
        if (newVal.length) {
          //해당 component에 created에서 이벤트버스 리스너가 활성화된 후 이벤트를 발생 시킨다
          this.$nextTick(() => {
            this.$eventBus.$emit(
              SIGNAL.DRAWING_FROM_VUEX,
              this.fileShareEventQueue.shift(), //첫번째 요소부터 실행 시키고, 제거
            )
          })
        } else setQueueAct(false) //큐 안에 데이터를 모두 처리한 후 큐는 비활성화 한다. (정상적으로 시그널데이터를 바로 이벤트로 발생시키도록)
      },
    },
  },

  /** created,beforeDestroy에 이벤트 버스 헨들러는 각 컴포넌트에 직접 구현한다 */

  destroyed() {
    //해당 component가 제거되면 다시 queue를 활성화 시키도록한다.
    setQueueAct(true)
  },
}
