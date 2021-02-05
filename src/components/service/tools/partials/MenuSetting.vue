<template>
  <menu-button
    :text="$t('service.setting')"
    :active="modalSetting"
    :disabled="!canSetting"
    :src="require('assets/image/ic_setting.svg')"
    @click="setting"
  ></menu-button>
</template>

<script>
import toolMixin from './toolMixin'
import toastMixin from 'mixins/toast'
import { mapGetters, mapActions } from 'vuex'
import { ROLE } from 'configs/remote.config'
export default {
  name: 'SettingMenu',
  mixins: [toolMixin, toastMixin],
  computed: {
    ...mapGetters(['allowLocalRecord', 'modalSetting', 'useTranslate']),
    canRecord() {
      if (this.disabled) {
        return false
      }
      if (this.account.roleType === ROLE.LEADER) {
        return true
      }
      if (this.isSafari) {
        return false
      }
      if (this.allowLocalRecord) {
        return true
      } else {
        return false
      }
    },
    canSetting() {
      if (!this.canRecord && !this.useTranslate) {
        return false
      } else {
        return true
      }
    },
  },
  methods: {
    ...mapActions(['showModalSetting']),
    setting() {
      if (!this.canSetting) {
        // TODO: MESSAGE
        this.toastDefault(this.$t('service.record_blocked'))
        return
      }
      this.showModalSetting(!this.modalSetting)
    },
  },
}
</script>
