<template>
  <section class="service-setting__view">
    <p class="service-setting--header">
      {{ $t('service.setting_pointing') }}
    </p>
    <div class="service-setting__row">
      <p class="service-setting__text">
        {{ $t('service.setting_pointing_participant') }}
      </p>

      <r-check
        :text="$t('service.setting_pointing_allow')"
        :value.sync="pointing"
      ></r-check>
    </div>
  </section>
</template>

<script>
import RCheck from 'RemoteCheckBox'

import toastMixin from 'mixins/toast'

import { mapGetters } from 'vuex'
import { ROLE, CONTROL } from 'configs/remote.config'

export default {
  name: 'ServiceSetPointing',
  mixins: [toastMixin],
  components: { RCheck },

  data() {
    return {
      initing: false,

      pointing: false,
    }
  },
  computed: {
    ...mapGetters(['allowPointing']),
  },
  watch: {
    pointing(flag) {
      if (this.initing === false) return
      this.$call.sendControl(CONTROL.POINTING, !!flag)
    },
  },
  methods: {
    init() {
      this.initing = false

      if (this.account.roleType === ROLE.LEADER) {
        this.pointing = this.allowPointing
      }
      this.$nextTick(() => {
        this.initing = true
      })
    },
  },
  created() {
    this.init()
  },
}
</script>
