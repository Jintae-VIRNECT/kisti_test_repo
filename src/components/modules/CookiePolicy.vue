<template>
  <div class="cookie-policy">
    <div class="cookie-policy-wrapper">
      <p class="cookie-policy-wrapper__text" v-html="text"></p>
      <button
        class="cookie-policy-wrapper__submit-button"
        @click="agreeCookie()"
      >
        {{ '확인' }}
      </button>
    </div>
  </div>
</template>
<script>
export default {
  name: 'CookiePolicy',
  methods: {
    agreeCookie() {
      this.$emit('update:visible', false)
    },
  },
  data() {
    return {
      text:
        "버넥트는 버넥트의 서비스를 운영하고 고객 경험을 개선하기 위해 쿠키를 사용합니다. 자세한 내용을 알아보려면<br>당사의 <a href='/policy/cookie' target='_blank'>쿠키 취급 방침</a>을 확인하세요. ‘확인’을 클릭하거나 사이트 사용을 계속하는 것은 이러한 쿠키 설정에 동의하는 것입니다.",
    }
  },
  /* Lifecycles */
  mounted() {
    localStorage.setItem('ServiceCookiesAgree', 1)
  },
}
</script>
<style lang="scss">
@import '~assets/style/mixin';

.cookie-policy {
  position: fixed;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 9999;
  background: #265da1;

  .cookie-policy-wrapper__text {
    color: #ffffff;
    font-size: 14px;

    a {
      color: #fff;
      text-decoration: underline;
      text-decoration-color: #ffffff;
    }
  }

  @include mobile {
    .cookie-policy-wrapper {
      display: block;
      padding: 18px 25px;
      text-align: right;
    }
    .cookie-policy-wrapper__text {
      font-size: 12px;
      text-align: left;
    }
    .cookie-policy-wrapper__submit-button {
      height: 34px;
      margin-top: 12px;
    }
  }
}

.cookie-policy-wrapper {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  max-width: 1080px;
  margin: auto;
  padding: 28px 25px;
  text-align: left;
}

.cookie-policy-wrapper__submit-button {
  width: 132px;
  height: 42px;
  color: #265da1;
  font-weight: 500;
  font-size: 15px;
  text-align: center;
  background: #ffffff;
  border: 1px solid #ccd1d7;
  border-radius: 2px;
  box-shadow: 0px 1px 1px 0px rgba(0, 0, 0, 0.05);
  &:hover {
    background-color: #c3c6ca;
    border: none;
  }
  &:active {
    background-color: #c4c6ca;
    border: none;
  }
  &:focus {
    background-color: #a4a8ae;
    border: none;
  }
}
</style>
