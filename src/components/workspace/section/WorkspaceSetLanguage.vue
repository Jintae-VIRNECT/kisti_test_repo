<template>
  <section>
    <div class="workspace-setting-title">언어 설정</div>

    <div class="radio-width">
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
          {
            text: '중국어',
            value: 'zh',
            imgSrc: require('assets/image/img_flag_CN.svg'),
          },
          {
            text: '일본어',
            value: 'jp',
            imgSrc: require('assets/image/img_flag_JP.svg'),
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

<style scoped>
.radio-width {
  width: 400px;
}
</style>
