<template>
  <section class="setting-section">
    <div class="setting-section__title">
      {{ '번역 설정' }}
    </div>
    <div class="setting-section__body horizon first">
      <figure class="setting__figure">
        <p class="setting__label">{{ '번역 사용' }}</p>
        <check :text="'번역 사용 허용'" :value.sync="useTranslate"></check>
      </figure>
      <figure class="setting__figure">
        <div class="setting__figure--wrapper">
          <p class="setting__label">
            {{ '번역 언어 설정' }}
          </p>
          <tooltip
            customClass="tooltip-guide"
            :content="'채팅 시, 번역 받을 국가 언어를 설정해주세요.'"
            placement="right"
            effect="blue"
          >
            <img
              slot="body"
              class="setting__tooltip--icon"
              src="~assets/image/ic_tool_tip.svg"
            />
          </tooltip>
        </div>
        <r-select
          class="setting__r-selecter"
          :options="radioOption"
          :disabled="!useTranslate"
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
import Check from 'Check'
import RSelect from 'RemoteSelect'
import Tooltip from 'Tooltip'
import { mapActions, mapGetters } from 'vuex'
import { languageCode } from 'utils/translate'

export default {
  name: 'WorkspaceSetTranslate',
  components: {
    Check,
    Tooltip,
    RSelect,
  },
  data() {
    return {
      useTranslate: false,
      transCode: 'ko',
      radioOption: languageCode,
    }
  },
  computed: {
    ...mapGetters(['translate']),
  },
  watch: {
    useTranslate(flag) {
      this.setTranslateFlag(flag)
    },
    transCode(language) {
      this.setTranslateCode(language)
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
    setTranslateCode(code) {
      this.setTranslate({ code })
      this.$localStorage.setTranslate('code', code)
    },
  },
  mounted() {
    this.useTranslate = this.translate.flag
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
