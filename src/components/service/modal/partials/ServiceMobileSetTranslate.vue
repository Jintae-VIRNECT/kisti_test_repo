<template>
  <div>
    <section class="setting-section list">
      <p v-if="showSTTwarning && isMobileSize" class="setting-view-stt-warning">
        {{ $t('workspace.setting_stt_safari_warning') }}
      </p>

      <div class="setting-section__title main">
        {{ $t('workspace.setting_translate') }}
      </div>
      <div class="setting-section__body horizon">
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
            <p class="setting__label" :class="{ disabled: !useTranslate }">
              {{ $t('workspace.setting_translate_language') }}
            </p>
            <tooltip
              :customClass="['tooltip-guide', { disabled: !useTranslate }]"
              :content="$t('workspace.setting_translate_language_tooltip')"
              :placement="isMobileDevice ? 'bottom' : 'right'"
              :effect="isMobileSize ? '' : 'blue'"
              :guide="true"
              @hide="helpIconActive = false"
              @active="helpIconActive = true"
            >
              <img
                slot="body"
                class="setting__tooltip--icon"
                :class="{ active: helpIconActive }"
                :src="
                  isMobileSize
                    ? require('assets/image/ic_tool_tip_new.svg')
                    : require('assets/image/ic_tool_tip.svg')
                "
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
      <div class="setting-section__title" :class="{ disabled: !useTranslate }">
        {{ $t('workspace.setting_translate_output') }}
      </div>
      <slider
        :first="$t('workspace.setting_translate_output_both')"
        :second="$t('workspace.setting_translate_output_each')"
        :isFirst.sync="transMultiple"
        :disabled="!useTranslate"
        @slider:disable="disableTranslate"
      ></slider>
    </section>
    <section class="setting-section list horizon translate">
      <figure v-if="!isMobileSize" class="setting-section__translate">
        <div
          class="setting-section__title"
          :class="{ disabled: !useTranslate }"
        >
          {{ $t('workspace.setting_stt') }}
        </div>
        <slider
          :first="$t('workspace.setting_stt_sync')"
          :second="$t('workspace.setting_stt_streaming')"
          :isFirst.sync="sttSync"
          :disabled="!useTranslate"
          @slider:disable="disableTranslate"
        ></slider>
      </figure>
      <figure class="setting-section__translate">
        <div
          class="setting-section__title"
          :class="{ disabled: !useTranslate }"
        >
          {{ $t('workspace.setting_tts') }}
        </div>
        <check
          :text="$t('workspace.setting_tts_allow')"
          :value.sync="ttsAllow"
          :disabled="!useTranslate"
          @check:disable="disableTranslate"
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
import toastMixin from 'mixins/toast'
import { mapActions, mapGetters } from 'vuex'

import { getIOSversion } from 'utils/appCheck'

export default {
  name: 'WorkspaceSetTranslate',
  mixins: [toastMixin],
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
      helpIconActive: false,
    }
  },
  computed: {
    ...mapGetters(['translate', 'languageCodes']),

    showSTTwarning() {
      const version = getIOSversion()

      return version > 0 && version < 14 ? true : false
    },
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
    disableTranslate() {
      this.toastDefault(this.$t('workspace.setting_translate_disable_message'))
    },
    setTranslateFlag(flag) {
      this.setTranslate({
        flag,
      })
      window.myStorage.setItemPiece('translate', 'flag', flag)
    },
    setTranslateCode(code) {
      this.setTranslate({ code })
      window.myStorage.setItemPiece('translate', 'code', code)
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
@import '~assets/style/mixin';
.radio-custom {
  max-width: 28.571rem;
  padding: 0.714rem 1.429rem;
  background-color: $color_bg_sub;
  border-radius: 2px;

  .radio-option:first-of-type {
    border-bottom: solid 1px rgba(#3d3d3d, 0.5);
  }
}

@include responsive-mobile {
  .setting__figure--wrapper {
    .setting__label {
      margin-right: 0.3rem;
    }
    .tooltip.tooltip-guide {
      margin-top: 0;
      margin-bottom: 1.1rem;

      &.disabled {
        opacity: 0.2;
        pointer-events: none;
      }

      .setting__tooltip--icon {
        width: 2rem;
        max-width: unset;
        height: 2rem;
        opacity: 0.5;
        transition: opacity 0.3s;
        &.active {
          opacity: 1;
        }
      }

      .tooltiptext {
        top: 160% !important;
        left: -9rem !important;
        display: flex;
        align-items: center;
        width: calc(100vw - 6.4rem) !important;
        height: 4.8rem;
        color: $new_color_text_main;
        background-color: $new_color_bg_active;
        @include fontLevel(75);
        .arrow {
          display: none;
        }
      }
    }
  }

  .setting-section {
    .setting-view-stt-warning {
      color: $color_red;
      @include fontLevel(75);
      margin-bottom: 15px;
    }
  }
}
</style>
