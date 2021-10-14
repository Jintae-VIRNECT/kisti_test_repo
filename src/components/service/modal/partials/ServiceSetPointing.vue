<template>
  <section class="service-setting__view">
    <p v-if="!isMobileSize" class="service-setting--header">
      {{ $t('service.setting_pointing') }}
    </p>

    <figure v-if="isMobileSize" class="setting__figure">
      <p class="setting__label">
        {{ $t('service.setting_pointing_participant') }}
      </p>
      <check
        :text="$t('service.setting_pointing_allow')"
        :value.sync="pointing"
      ></check>
    </figure>

    <div v-else class="service-setting__row">
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
import Check from 'Check'

import toastMixin from 'mixins/toast'

import { mapGetters } from 'vuex'
import { ROLE, CONTROL } from 'configs/remote.config'

export default {
  name: 'ServiceSetPointing',
  mixins: [toastMixin],
  components: { Check, RCheck },

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

<style lang="scss">
@import '~assets/style/mixin';

@include responsive-mobile {
  .service-setting__view {
    width: 100%;
    padding: 2rem 1.6rem;

    .setting__figure {
      .setting__label {
        @include fontLevel(100);
        margin-bottom: 0.8rem;
      }
      .check {
        @include responsive-check-toggle;
      }
    }
  }
}
</style>
