<template>
  <tool-button
    class="ar-3d-content-btn"
    :text="$t('service.ar_3d')"
    :disabled="disabled"
    :active="active"
    :disableTooltip="disableTooltip"
    :src="require('assets/image/call/mdpi_icon_3D.svg')"
    @click.native="clickHandler"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import { VIEW, ACTION } from 'configs/view.config'
import { AR_3D_CONTENT_SHARE } from 'configs/remote.config'
import { mapGetters } from 'vuex'

export default {
  name: 'ToolAr3dContent',
  mixins: [toolMixin],
  computed: {
    ...mapGetters(['mainView', 'viewAction']),
    active() {
      return this.isMobileSize ? false : this.viewAction === ACTION.AR_3D
    },
  },
  methods: {
    clickHandler() {
      if (this.disabled) return
      if (this.view !== VIEW.AR) return
      if (this.viewAction === ACTION.AR_3D) return

      this.activate3dShareMode()
    },
    activate3dShareMode() {
      this.setAction(ACTION.AR_3D)

      const targetUserId = this.mainView.id

      //시그널 전송 : start 3D contents share
      this.$call.sendAr3dSharing(AR_3D_CONTENT_SHARE.START_SHARE, {
        targetUserId,
      })
    },
  },
}
</script>
<style lang="scss">
.ar-3d-content-btn > .tool > img {
  width: 3.571rem;
  object-fit: none;
}

.mobile-ar-tools-btn.ar-3d-content-btn > .tool > img {
  width: 100%;
}
</style>
