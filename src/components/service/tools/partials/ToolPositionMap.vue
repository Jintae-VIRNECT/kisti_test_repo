<template>
  <tool-button
    :text="$t('service.map_information')"
    :active="status"
    :disabled="!isAvailable"
    :src="require('assets/image/call/ic_location.svg')"
    @click="requestLocation"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import toastMixin from 'mixins/toast'
import { DEVICE } from 'configs/device.config'

import { mapGetters } from 'vuex'
export default {
  name: 'ToolPositionMap',
  mixins: [toolMixin, toastMixin],

  data() {
    return {
      status: false,
      isAvailable: true,
    }
  },
  computed: {
    ...mapGetters(['mainView']),
  },
  watch: {
    mainView: {
      handler() {
        this.isAvailable =
          this.mainView.deviceType === DEVICE.MOBILE ||
          this.mainView.deviceType === DEVICE.GLASSES ||
          this.mainView.deviceType === DEVICE.FITT360
      },
      deep: true,
      immediate: true,
    },
  },

  methods: {
    requestLocation() {
      if (!this.isAvailable) {
        this.toastDefault(this.$t('service.map_cannot_use_gps'))
        return
      }

      this.showMap()
      this.$call.sendRequestLocation(false, [this.mainView.connectionId])
    },
    rejected(enable) {
      if (!enable) {
        this.toastDefault(this.$t('service.map_request_rejected'))
      }
    },
    showMap() {
      this.status = true
      this.$eventBus.$emit('map:show', this.status)
    },
    closeMap() {
      this.status = false
    },
  },
  mounted() {
    this.$eventBus.$on('map:closed', this.closeMap)
    this.$eventBus.$on('map:enable', this.rejected)
  },
  beforeDestroy() {
    this.$eventBus.$off('map:closed', this.closeMap)
    this.$eventBus.$off('map:enable', this.rejected)
  },
}
</script>
