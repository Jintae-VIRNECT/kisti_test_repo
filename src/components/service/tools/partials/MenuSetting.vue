<template>
  <menu-button
    text="캡쳐 후 공유"
    :active="action === 'setting'"
    :src="require('assets/image/ic_setting.svg')"
    @click="setting"
  ></menu-button>
</template>

<script>
import toolMixin from './toolMixin'
export default {
  name: 'SettingMenu',
  mixins: [toolMixin],
  data() {
    return {}
  },
  computed: {},
  watch: {},
  methods: {
    setting() {
      this.setAction('setting')
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
