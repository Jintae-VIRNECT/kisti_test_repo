<template>
  <div>
    <menu-button
      text="로컬 녹화 설정"
      :active="status"
      :src="require('assets/image/ic_setting.svg')"
      @click="setting"
    ></menu-button>
    <service-local-record-setting
      :visible.sync="showSetting"
    ></service-local-record-setting>
  </div>
</template>

<script>
import toolMixin from './toolMixin'
import ServiceLocalRecordSetting from 'components/workspace/modal/ServiceLocalRecordSetting'
export default {
  name: 'SettingMenu',
  mixins: [toolMixin],
  data() {
    return {
      showSetting: false,
      status: false,
    }
  },
  components: {
    ServiceLocalRecordSetting,
  },
  computed: {},
  watch: {},
  methods: {
    setting() {
      this.status = !this.status
      this.showSetting = true
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
