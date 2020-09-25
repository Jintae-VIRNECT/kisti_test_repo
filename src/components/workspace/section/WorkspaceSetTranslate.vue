<template>
  <section class="setting-section">
    <div class="setting-section__title">
      {{ '번역 설정' }}
    </div>
    <div class="setting-section__body horizon first">
      <figure class="setting__figure">
        <switcher :text="'번역 사용'" :value.sync="useTranslate"></switcher>
      </figure>
    </div>
  </section>
</template>
<script>
import Switcher from 'Switcher'
import { mapActions, mapGetters } from 'vuex'

export default {
  name: 'WorkspaceSetTranslate',
  components: {
    Switcher,
  },
  data() {
    return {
      useTranslate: false,
    }
  },
  computed: {
    ...mapGetters(['translate']),
  },
  watch: {
    useTranslate(flag) {
      this.setTranslateFlag(flag)
    },
  },
  methods: {
    ...mapActions(['setTranslate']),
    setTranslateFlag(flag) {
      this.setTranslate({
        flag,
      })
      this.$localStorage.setTranslate('flag', flag)
    },
  },
  mounted() {
    this.useTranslate = this.translate.flag
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';
.radio-custom {
  max-width: 28.571rem;
  padding: 0.714rem 1.429rem;
  background-color: $color_bg_sub;
  border-radius: 2px;

  .radio-option:first-of-type {
    border-bottom: solid 1px rgba(#3d3d3d, 0.5);
  }
}
</style>
