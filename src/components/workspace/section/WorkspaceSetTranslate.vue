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
              :guide="true"
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
        {{ $t('workspace.setting_translate_output') }}
      </div>
      <slider
        :first="$t('workspace.setting_translate_output_both')"
        :second="$t('workspace.setting_translate_output_each')"
        :isFirst.sync="transMultiple"
      ></slider>
    </section>
    <section class="setting-section list horizon translate">
      <figure class="setting-section__translate">
        <div class="setting-section__title">
          {{ $t('workspace.setting_stt') }}
        </div>
        <slider
          :first="$t('workspace.setting_stt_sync')"
          :second="$t('workspace.setting_stt_streaming')"
          :isFirst.sync="sttSync"
        ></slider>
      </figure>
      <figure class="setting-section__translate">
        <div class="setting-section__title">
          {{ $t('workspace.setting_tts') }}
        </div>
        <check
          :text="$t('workspace.setting_tts_allow')"
          :value.sync="ttsAllow"
        ></check>
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
      transMultiple: true,
      sttSync: true,
      ttsAllow: false,
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
    transMultiple(flag) {
      this.setTranslateMultiple(flag)
    },
    sttSync(flag) {
      this.setSttSync(flag)
    },
    ttsAllow(flag) {
      this.setTtsAllow(flag)
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
    setTranslateMultiple(multiple) {
      console.log(multiple)
      this.setTranslate({ multiple })
      this.$localStorage.setTranslate('multiple', multiple)
    },
    setSttSync(sttSync) {
      this.setTranslate({ sttSync })
      this.$localStorage.setTranslate('sttSync', sttSync)
    },
    setTtsAllow(ttsAllow) {
      this.setTranslate({ ttsAllow })
      this.$localStorage.setTranslate('ttsAllow', ttsAllow)
    },
  },
  created() {
    this.useTranslate = this.translate.flag
    this.transCode = this.translate.code
    this.transMultiple = this.translate.multiple
    this.sttSync = this.translate.sttSync
    this.ttsAllow = this.translate.ttsAllow
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
