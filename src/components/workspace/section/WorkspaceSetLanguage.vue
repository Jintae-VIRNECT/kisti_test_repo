<template>
  <section class="workspace-setting-section">
    <div class="workspace-setting-title">언어 설정</div>
    <div class="workspace-setting-label">
      사용하고자하는 언어를 선택해주세요.
    </div>
    <div>
      <div class="radio-country">
        <input type="radio" id="en" name="lang" value="en" v-model="picked" />
        <label for="en">English, US</label>
        <span>
          <img class="country-flag" src="~assets/image/setting/mdpi_USA.svg" />
        </span>
      </div>
      <div class="radio-country">
        <input type="radio" id="kr" name="lang" value="kr" v-model="picked" />
        <label for="kr">한국어</label>
        <span>
          <img
            class="country-flag"
            src="~assets/image/setting/mdpi_korea.svg"
          />
        </span>
      </div>
      <div class="radio-country">
        <input type="radio" id="zh" name="lang" value="zh" v-model="picked" />
        <label for="zh">중국어(중국)</label>
        <span>
          <img class="country-flag" src="~assets/image/setting/mdpi_CN.svg" />
        </span>
      </div>
      <div class="radio-country">
        <input type="radio" id="jp" name="lang" value="jp" v-model="picked" />
        <label for="jp">일본어</label>
        <span>
          <img class="country-flag" src="~assets/image/setting/mdpi_JP.svg" />
        </span>
      </div>
    </div>
  </section>
</template>
<script>
export default {
  data: function() {
    return {
      picked: '',
    }
  },
}
</script>

<style scoped>
.radio-country {
  display: flex;
  justify-content: space-between;
  margin-bottom: 25px;
}
.country-flag {
  width: 25px;
  height: 15px;
}

[type='radio']:checked,
[type='radio']:not(:checked) {
  position: absolute;
  left: -9999px;
  font-weight: 500;
}
[type='radio']:checked + label,
[type='radio']:not(:checked) + label {
  position: relative;
  padding-left: 28px;
  cursor: pointer;
  line-height: 20px;
  display: inline-block;
  color: rgb(255, 255, 255);
  font-family: 'NotoSansCJKkr-Regular';
  font-weight: normal;
}
[type='radio']:checked + label:before,
[type='radio']:not(:checked) + label:before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 18px;
  height: 18px;
  border: 1px solid #979797;
  border-radius: 100%;
  background: #1f1e1e;
}
[type='radio']:checked + label:after,
[type='radio']:not(:checked) + label:after {
  content: '';
  width: 12px;
  height: 12px;
  background: #0f75f5;
  position: absolute;
  top: 4px;
  left: 4px;
  border-radius: 100%;
  -webkit-transition: all 0.2s ease;
  transition: all 0.2s ease;
}
[type='radio']:not(:checked) + label:after {
  opacity: 0;
  -webkit-transform: scale(0);
  transform: scale(0);
}
[type='radio']:checked + label:before {
  border: 1px solid #0f75f5;
  font-family: 'NotoSansCJKkr-Medium';
  font-weight: 500;
}
</style>
