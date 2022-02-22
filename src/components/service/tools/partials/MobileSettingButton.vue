<template>
  <button class="setting-button" @click="setting"></button>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
export default {
  name: 'MobileSettingButton',
  computed: {
    ...mapGetters(['modalSetting']),
  },
  methods: {
    ...mapActions(['showModalSetting']),
    setting() {
      this.$eventBus.$emit('popover:close')
      this.showModalSetting(!this.modalSetting)
    },
  },
}
</script>

<style lang="scss" scoped>
.setting-button {
  width: 3.6rem;
  height: 3.6rem;
  background: url(~assets/image/call/mdpi_icon_setting_new.svg) center no-repeat;
}
</style>
