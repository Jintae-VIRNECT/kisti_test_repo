<template>
  <div>
    <menu-button
      text="로컬 녹화 설정"
      :active="status"
      :src="require('assets/image/ic_setting.svg')"
      @click="setting"
    ></menu-button>
  </div>
</template>

<script>
import toolMixin from './toolMixin'
export default {
  name: 'SettingMenu',
  mixins: [toolMixin],
  data() {
    return {
      status: false,
    }
  },
  components: {},
  computed: {},
  watch: {},
  methods: {
    setting() {
      this.status = !this.status
      this.$eventBus.$emit('lcRecSet:show')
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
