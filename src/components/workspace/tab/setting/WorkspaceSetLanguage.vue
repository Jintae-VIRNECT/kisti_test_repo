<template>
  <section class="setting-section language">
    <div class="setting-section__title main">
      {{ $t('workspace.setting_language_choice') }}
    </div>
    <div class="radio-custom">
      <r-radio
        :options="radioOption.options"
        :text="radioOption.text"
        :value="radioOption.value"
        :selectedOption.sync="selectLanguage"
      ></r-radio>
    </div>
  </section>
</template>
<script>
import RRadio from 'RemoteRadio'

import langMixin from 'mixins/language'

export default {
  name: 'WorkspaceSetLanguage',
  mixins: [langMixin],
  props: {},
  data() {
    const defaultLanguage = this.mx_getLangCode()

    return {
      selectLanguage: defaultLanguage,
      picked: '',
      radioOption: {
        options: [
          {
            text: '한국어',
            value: 'ko',
            imgSrc: require('assets/image/img_flag_korea.svg'),
          },
          {
            text: 'English',
            value: 'en',
            imgSrc: require('assets/image/img_flag_USA.svg'),
          },
          {
            text: '日本語',
            value: 'ja',
            imgSrc: require('assets/image/img_flag_JP.svg'),
          },
          {
            text: '简体中文(翻译)',
            value: 'zh-cn',
            imgSrc: require('assets/image/img_flag_CN.svg'),
          },
          {
            text: '繁體中文(譯本)',
            value: 'zh-tw',
            imgSrc: require('assets/image/img_flag_CN.svg'),
          },
          {
            text: 'Español',
            value: 'es',
            imgSrc: require('assets/image/img_flag_ES.svg'),
          },
        ],
        text: 'text',
        value: 'value',
        selectedOption: 'en',
      },
    }
  },
  components: {
    RRadio,
  },
  watch: {
    selectLanguage(language) {
      this.mx_changeLang(language)
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';
@import '~assets/style/mixin';
.radio-custom {
  max-width: 28.571rem;
  padding: 0.714rem 1.429rem;
  background-color: $color_bg_sub;
  border-radius: 2px;

  .radio-option {
    border-bottom: solid 1px rgba(#3d3d3d, 0.5);
    // border-bottom: transparent;
    &:last-child {
      border-bottom: none;
    }
  }
}

@include responsive-mobile {
  .radio-custom {
    .radio-option {
      display: flex;
      align-items: center;
      height: 5.6rem;
      @include fontLevel(100);
      color: $new_color_text_sub;
      border-bottom: 1.5px solid $new_color_sub_border;

      &.active {
        color: $new_color_text_main;
      }

      .radio-option__input {
        width: 2.4rem;
        height: 2.4rem;
        &.active::before {
          width: 1.4rem;
          height: 1.4rem;
        }
      }
    }
  }
}
</style>
