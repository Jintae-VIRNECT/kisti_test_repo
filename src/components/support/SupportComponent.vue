<template>
  <div class="support">
    <div class="support--body">
      <div class="support--graphic">
        <img
          src="~assets/image/support/mdpi_404.svg"
          alt="Browser is not supported"
        />
      </div>
      <h2 class="support--title" v-html="$t('support.header_title')"></h2>
      <p
        class="support--description"
        v-html="$t('support.header_description')"
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

    <footer class="footer" v-if="isScreenDesktop">
      <div class="footer--inner">
        <p class="copyright">
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
