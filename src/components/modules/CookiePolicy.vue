<template>
  <div class="cookie-policy">
    <div class="cookie-policy-wrapper">
      <!-- pc/태블릿 용 문구 -->
      <p
        class="cookie-policy-wrapper__text"
        v-html="$t('workspace.cookie')"
      ></p>

      <!-- 모바일용 문구 : 문구 줄바꿈 형태가 달라 분기 처리 -->
      <p
        class="cookie-policy-wrapper-mobile__text"
        v-html="$t('workspace.cookie_mobile')"
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

  .cookie-policy-wrapper__text,
  .cookie-policy-wrapper-mobile__text {
    > a {
      color: #fff;
      text-decoration: underline;
      text-decoration-color: #ffffff;
    }
  }

  .cookie-policy-wrapper__text {
    color: #ffffff;
    font-size: 1rem;
  }

  //모바일 용 문구 비활성화 처리
  .cookie-policy-wrapper-mobile__text {
    display: none;
    word-break: break-all;
    @include fontLevel(150);
  }

  //legacy
  // @include mobile {
  //   .cookie-policy-wrapper {
  //     display: block;
  //     padding: 1.2857rem 1.7857rem;
  //     text-align: right;
  //   }
  //   .cookie-policy-wrapper__text {
  //     font-size: 0.8571rem;
  //     text-align: left;
  //   }
  //   .cookie-policy-wrapper__submit-button {
  //     height: 2.4286rem;
  //     margin: 0.8571rem 0 0 0;
  //   }
  // }
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

@include responsive-mobile {
  .cookie-policy {
    background-color: $new_color_bg_active;
    border-radius: 1rem 1rem 0 0;
  }
  .cookie-policy-wrapper {
    display: flex;
    flex-direction: column;
    padding: 2.4rem 1.6rem 1.6rem 1.6rem;

    .cookie-policy-wrapper__text {
      display: none;
    }

    //모바일 용 문구 표시
    .cookie-policy-wrapper-mobile__text {
      display: block;
    }

    .cookie-policy-wrapper__submit-button {
      width: 100%;
      height: 4.8rem;
      margin-top: 3.2rem;
      @include fontLevel(150);
      color: $new_color_text_blue;
      &:hover,
      &:active,
      &:focus {
        background-color: white;
      }
    }
  }
}
</style>
