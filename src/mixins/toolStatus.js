export default {
  data() {
    return {
      toolStatus: {
        undo: false,
        redo: false,
        clear: false,
        clearAll: false,
      },
    }
  },
  methods: {
    //드로잉, 포인팅 여부에 따라 tool 상태 업데이트 이벤트를 수신해서 해당 툴의 disabled 여부를 최종 결정한다.
    activateToolStatusUpdateListener() {
      this.$eventBus.$on(`tool:undo`, this.setUndoAvailable)
      this.$eventBus.$on(`tool:redo`, this.setRedoAvailable)
      this.$eventBus.$on(`tool:clear`, this.setClearAvailable)
      this.$eventBus.$on(`tool:clearall`, this.setClearAllAvailable)
    },
    deactivateToolStatusUpdateListener() {
      this.$eventBus.$off(`tool:undo`, this.setUndoAvailable)
      this.$eventBus.$off(`tool:redo`, this.setRedoAvailable)
      this.$eventBus.$off(`tool:clear`, this.setClearAvailable)
      this.$eventBus.$off(`tool:clearall`, this.setClearAllAvailable)
    },
    setUndoAvailable(available) {
      this.toolStatus.undo = available
    },
    setRedoAvailable(available) {
      this.toolStatus.redo = available
    },
    setClearAvailable(available) {
      this.toolStatus.clear = available
    },
    setClearAllAvailable(available) {
      this.toolStatus.clearAll = available
    },
  },
}
