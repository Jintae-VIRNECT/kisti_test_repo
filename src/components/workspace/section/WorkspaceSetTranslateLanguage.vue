<template>
  <section class="setting-section">
    <div class="setting-section__title">
      {{ '번역 언어 설정' }}
    </div>
    <div class="setting-section__body horizon first">
      <figure class="setting__figure">
        <p class="setting__label">
          {{ '채팅 시, 번역 받을 국가 언어를 설정해주세요.' }}
        </p>
        <r-select
          class="setting__r-selecter"
          :options="radioOption"
          value="code"
          text="name"
          :selectedValue.sync="transCode"
        >
        </r-select>
      </figure>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import { mapActions, mapGetters } from 'vuex'

import { languageCode } from 'utils/translate'

export default {
  name: 'WorkspaceSetTranslateLanguage',
  components: {
    RSelect,
  },
  data() {
    return {
      transCode: 'ko',
      radioOption: languageCode,
    }
  },
  computed: {
    ...mapGetters(['translate']),
  },
  watch: {
    transCode(language) {
      this.setTranslateCode(language)
    },
  },
  methods: {
    ...mapActions(['setTranslate']),
    setTranslateCode(code) {
      this.setTranslate({ code })
      this.$localStorage.setTranslate('code', code)
    },
  },
  mounted() {
    this.transCode = this.translate.code
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
