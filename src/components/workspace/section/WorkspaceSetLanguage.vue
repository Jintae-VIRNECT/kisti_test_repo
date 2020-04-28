<template>
  <section>
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
export default {
  props: {
    defaultLanguage: {
      type: String,
      default: 'en',
    },
  },
  data: function() {
    return {
      selectLanguage: this.defaultLanguage,
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
    selectLanguage: function(language) {
      this.$store.dispatch('setLanguage', language)
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';
.radio-custom {
  width: 400px;
  padding: 10px 20px 10px 20px;
  background-color: $color_bg_sub;
  border-radius: 2px;

  .radio-option:first-of-type {
    border-bottom-color: rgba(61, 61, 61, 0.5);
    border-bottom-width: 1px;
    border-bottom-style: solid;
  }
}
</style>
