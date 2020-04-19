<template>
  <section>
    <div class="workspace-setting-title">언어 선택</div>
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
  data: function() {
    return {
      selectLanguage: 'en',
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
      this.$emit('selectedLanguage', { language: language })
    },
  },
}
</script>

<style lang="scss">
.radio-custom {
  width: 400px;
  padding-top: 10px;
  padding-right: 20px;
  padding-bottom: 10px;
  padding-left: 20px;
  background-color: rgb(36, 36, 39);
  border-radius: 2px;

  .radio-option:first-of-type {
    border-bottom-color: rgba(61, 61, 61, 0.5);
    border-bottom-width: 1px;
    border-bottom-style: solid;
  }
}
</style>
