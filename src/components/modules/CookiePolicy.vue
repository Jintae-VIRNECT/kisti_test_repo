<template>
  <div class="cookie-policy">
    <div class="cookie-policy-wrapper">
      <p
        class="cookie-policy-wrapper__text"
        v-html="$t('workspace.cookie')"
      ></p>
      <button
        class="cookie-policy-wrapper__submit-button"
        @click="agreeCookie()"
      >
        {{ $t('button.confirm') }}
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
    font-size: 1rem;

    > a {
      color: #fff;
      text-decoration: underline;
      text-decoration-color: #ffffff;
    }
  }

  @include mobile {
    .cookie-policy-wrapper {
      display: block;
      padding: 1.2857rem 1.7857rem;
      text-align: right;
    }
    .cookie-policy-wrapper__text {
      font-size: 0.8571rem;
      text-align: left;
    }
    .cookie-policy-wrapper__submit-button {
      height: 2.4286rem;
      margin: 0.8571rem 0 0 0;
    }
  }
}

.cookie-policy-wrapper {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  max-width: 1080px;
  margin: auto;
  padding: 2rem 1.7857rem;
  text-align: left;
}

.cookie-policy-wrapper__submit-button {
  width: 9.4286rem;
  height: 3rem;
  color: #265da1;
  font-weight: 500;
  font-size: 1.0714rem;
  text-align: center;
  background: #ffffff;
  border: 1px solid #ccd1d7;
  border-radius: 2px;
  box-shadow: 0px 1px 1px 0px rgba(#000000, 0.05);
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
