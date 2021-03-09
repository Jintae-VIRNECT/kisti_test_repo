<template>
  <section class="service-setting__view">
    <p class="service-setting--header">
      {{ $t('service.setting_translate') }}
    </p>
    <div class="service-setting__row">
      <p class="service-setting__text">
        {{ $t('service.setting_translate_use') }}
      </p>
      <r-check
        :text="$t('service.setting_translate_use_allow')"
        :value.sync="useTranslate"
      ></r-check>
    </div>
    <div class="service-setting__row">
      <div
        class="service-setting__text custom"
        :class="{ disable: !useTranslate }"
      >
        <p>{{ $t('service.setting_translate_language') }}</p>
        <tooltip
          customClass="tooltip-guide"
          :content="$t('service.setting_translate_language_tooltip')"
          :placement="isTablet ? 'bottom' : 'right'"
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
        class="service-setting__selector"
        :options="languageCodes"
        value="code"
        text="text"
        :disabled="!useTranslate"
        :selectedValue.sync="transCode"
        :targetElement="'.modal'"
      >
      </r-select>
    </div>
    <div class="service-setting__row">
      <div class="service-setting__text" :class="{ disable: !useTranslate }">
        {{ $t('workspace.setting_translate_output') }}
      </div>
      <slider
        :first="$t('workspace.setting_translate_output_both')"
        :second="$t('workspace.setting_translate_output_each')"
        :isFirst.sync="transMultiple"
        :disabled="!useTranslate"
        @slider:disable="disableTranslate"
      ></slider>
    </div>
    <div class="service-setting__row">
      <div class="service-setting__text" :class="{ disable: !useTranslate }">
        {{ $t('workspace.setting_stt') }}
      </div>
      <slider
        :first="$t('workspace.setting_stt_sync')"
        :second="$t('workspace.setting_stt_streaming')"
        :isFirst.sync="sttSync"
        :disabled="!useTranslate"
        @slider:disable="disableTranslate"
      ></slider>
    </div>
    <div class="service-setting__row">
      <div class="service-setting__text" :class="{ disable: !useTranslate }">
        {{ $t('workspace.setting_tts') }}
      </div>
      <check
        :text="$t('workspace.setting_tts_allow')"
        :value.sync="ttsAllow"
        :disabled="!useTranslate"
        @check:disable="disableTranslate"
      ></check>
    </div>
  </section>
</template>

<script>
import RSelect from 'RemoteSelect'
import Check from 'Check'
import Tooltip from 'Tooltip'
import Slider from 'Slider'
import RCheck from 'RemoteCheckBox'

import toastMixin from 'mixins/toast'

import { mapGetters, mapActions } from 'vuex'

export default {
  name: 'ServiceSetTranslate',
  mixins: [toastMixin],
  components: {
    RSelect,
    RCheck,
    Check,
    Tooltip,
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
    transCode(language) {
      this.setTranslateCode(language)
    },
    transMultiple(flag) {
      this.setTranslateMultiple(flag)
    },
    useTranslate(flag) {
      this.setTranslateFlag(flag)
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
    changeTranslate(item, setting) {
      const param = {}
      param[item] = setting
      this.setTranslate(param)
      window.myStorage.setItemPiece('translate', item, setting)
      // this.showToast()
    },
    setTranslateCode(code) {
      this.setTranslate({ code })
      window.myStorage.setItemPiece('translate', 'code', code)
    },
    disableTranslate() {
      this.toastDefault(this.$t('workspace.setting_translate_disable_message'))
    },
    setTranslateFlag(flag) {
      this.setTranslate({
        flag,
      })
      window.myStorage.setItemPiece('translate', 'flag', flag)
    },
    setTranslateMultiple(multiple) {
      this.setTranslate({ multiple })
      window.myStorage.setItemPiece('translate', 'multiple', multiple)
    },
    setSttSync(sttSync) {
      this.setTranslate({ sttSync })
      window.myStorage.setItemPiece('translate', 'sttSync', sttSync)
    },
    setTtsAllow(ttsAllow) {
      this.setTranslate({ ttsAllow })
      window.myStorage.setItemPiece('translate', 'ttsAllow', ttsAllow)
    },
    init() {
      this.useTranslate = this.translate.flag
      this.transCode = this.translate.code
      this.transMultiple = this.translate.multiple
      this.sttSync = this.translate.sttSync
      this.ttsAllow = this.translate.ttsAllow
    },
  },
  created() {
    this.init()
  },
}
</script>

<style></style>
