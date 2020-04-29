<template>
  <div class="support">
    <div class="body">
      <div class="body__graphic">
        <img
          src="~assets/image/support/img_support_404.svg"
          alt="Browser is not supported"
        />
      </div>
      <h2 class="body__title" v-html="$t('support.header_title')"></h2>
      <p
        class="body--description"
        v-html="$t('support.header_description')"
      ></p>
      <p
        class="body--mobile"
        v-html="$t('support.header_description_mobile')"
      ></p>
      <div>
        <el-button v-if="isScreenDesktop" @click="pcWeb">{{
          $t('support.pc_web_button')
        }}</el-button>
        <el-button v-else @click="mobile">{{
          $t('support.mobile_button')
        }}</el-button>
      </div>
    </div>

    <footer class="footer">
      <div class="footer__inner">
        <p class="footer__copyright">
          Copyright&copy; <strong>VIRNECT INC</strong>. All Rights Reserved.
        </p>
      </div>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'SupportComponent',
  methods: {
    pcWeb() {
      window.open('https://www.google.com/intl/ko/chrome/')
    },
    mobile() {
      window.open(
        'https://play.google.com/store/apps/details?id=com.virnect.remote.mobile&hl=ko',
      )
    },
    smartGlasses() {
      //미정
    },
  },
}
</script>
<style lang="scss" src="assets/style/support.scss"></style>
