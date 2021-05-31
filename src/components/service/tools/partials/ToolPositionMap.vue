<template>
  <tool-button
    :text="'지도'"
    :active="status"
    :isActive="status"
    :disabled="false"
    :src="require('assets/image/ic_notice.svg')"
    :activeSrc="require('assets/image/ic_notice_off.svg')"
    @click="toggleMap"
  ></tool-button>
</template>

<script>
import toolMixin from './toolMixin'
import toastMixin from 'mixins/toast'
// import { CAMERA as CAMERA_STATUS } from 'configs/device.config'
// import { mapActions, mapGetters } from 'vuex'
export default {
  name: 'ToolPositionMap',
  mixins: [toolMixin, toastMixin],

  data() {
    return {
      status: false,
    }
  },
  computed: {
    // ...mapGetters(['myInfo', 'myTempStream', 'settingInfo', 'video']),
  },

  methods: {
    toggleMap() {
      this.status = !this.status
      this.$eventBus.$emit('show:positionmap', this.status)
    },
  },
  mounted() {
    this.$eventBus.$on('close:positionmap', () => {
      this.status = false
    })
  },
}
</script>
