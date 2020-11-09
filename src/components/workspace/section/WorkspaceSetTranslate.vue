<template>
  <div>
    <section class="setting-section list">
      <div class="setting-section__title">
        {{ $t('workspace.setting_translate') }}
      </div>
      <div class="setting-section__body horizon first">
        <figure class="setting__figure">
          <p class="setting__label">
            {{ $t('workspace.setting_translate_use') }}
          </p>
          <check
            :text="$t('workspace.setting_translate_use_allow')"
            :value.sync="useTranslate"
          ></check>
        </figure>
        <figure class="setting__figure">
          <div class="setting__figure--wrapper">
            <p class="setting__label">
              {{ $t('workspace.setting_translate_language') }}
            </p>
            <tooltip
              customClass="tooltip-guide"
              :content="$t('workspace.setting_translate_language_tooltip')"
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
            :options="languageCodes"
            :disabled="!useTranslate"
            value="code"
            text="text"
            :selectedValue.sync="transCode"
          >
          </r-select>
        </figure>
      </div>
    </section>
    <section class="setting-section list">
      <div class="setting-section__title">
        {{ '번역/원본 출력 방식' }}
      </div>
      <slider
        :first="'동시 출력'"
        :second="'선택 출력'"
        :isFirst.sync="transSameTime"
      ></slider>
    </section>
    <section class="setting-section list horizon">
      <figure class="setting-section__translate">
        <div class="setting-section__title">
          {{ '음성 인식 방식(STT)' }}
        </div>
        <slider
          :first="'음성 녹음'"
          :second="'스트리밍'"
          :isFirst.sync="sttSamePeriod"
        ></slider>
      </figure>
      <figure class="setting-section__translate">
        <div class="setting-section__title">
          {{ '음성 변환 설정(TTS)' }}
        </div>
        <check :text="'음성 변환 사용 허용'" :value.sync="useTranslate"></check>
      </figure>
    </section>
  </div>
</template>
<script>
import Check from 'Check'
import RSelect from 'RemoteSelect'
import Slider from 'Slider'
import Tooltip from 'Tooltip'
import { mapActions, mapGetters } from 'vuex'

export default {
  name: 'WorkspaceSetTranslate',
  components: {
    Check,
    Tooltip,
    RSelect,
    Slider,
  },
  data() {
    return {
      useTranslate: false,
      transCode: 'ko-KR',
      transSameTime: true,
      sttSamePeriod: true,
    }
  },
  computed: {
    ...mapGetters(['translate', 'languageCodes']),
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
  created() {
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
