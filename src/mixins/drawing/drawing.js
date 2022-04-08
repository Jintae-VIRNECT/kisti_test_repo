import { mapGetters } from 'vuex'
import { DRAWING } from 'configs/remote.config'
import { VIEW } from 'configs/view.config'
import toastMixin from 'mixins/toast'

export default {
  mixins: [toastMixin],
  computed: {
    ...mapGetters(['shareFile']),
  },

  methods: {
    $_exitDrawing() {
      const isSharingFile = this.shareFile && this.shareFile.id
      if (!isSharingFile) return

      this.confirmCancel(this.$t('service.toast_exit_drawing'), {
        text: this.$t('button.exit'),
        action: () => {
          this.$call.sendDrawing(DRAWING.END_DRAWING)
          this.showImage({})
          this.setView(VIEW.STREAM)
          this.$eventBus.$emit(`control:${VIEW.DRAWING}:clearall`)

          this.toastDefault(this.$t('service.toast_drawing_end'))
        },
      })
    },
  },
}
