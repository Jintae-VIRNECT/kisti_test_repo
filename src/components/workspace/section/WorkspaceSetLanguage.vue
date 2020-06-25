<template>
  <section class="setting-section">
    <div class="setting__title">언어 선택</div>
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
            text: 'English',
            value: 'en',
            imgSrc: require('assets/image/img_flag_USA.svg'),
          },
          {
            text: '한국어',
            value: 'ko',
            imgSrc: require('assets/image/img_flag_korea.svg'),
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
  mounted() {
    const lang = localStorage.getItem('language')
    if (lang) {
      this.mx_changeLang(lang)
    }
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
