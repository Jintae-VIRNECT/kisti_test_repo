<template>
  <div class="support">
    <div class="support-body">
      <div class="support-body__graphic">
        <img
          v-if="isScreenDesktop"
          src="~assets/image/support/img_support_404.svg"
          alt="Browser is not supported"
        />
        <img
          v-else
          src="~assets/image/support/img_support_404_m.svg"
          alt="Browser is not supported"
        />
      </div>
      <h2 class="support-body__title" v-html="$t('support.header_title')"></h2>
      <p
        class="support-body--description"
        v-html="
          isOnpremise ? $t('support.pc_web') : $t('support.header_description')
        "
      ></p>
      <p
        class="support-body--mobile"
        v-html="
          isOnpremise
            ? $t('support.pc_web')
            : $t('support.header_description_mobile')
        "
      ></p>
      <div>
        <button
          class="support-body--button"
          v-if="isScreenDesktop && !isOnpremise"
          @click="pcWeb"
        >
          {{ $t('support.pc_web_button') }}
        </button>
      </div>
    </div>

    <footer class="support-footer" v-if="!isOnpremise">
      <div class="support-footer__inner">
        <p class="support-footer__copyright">
          &copy; VIRNECT CO., LTD. All rights reserved.
        </p>
      </div>
    </footer>
  </div>
</template>

<script>
import { getConfigs } from 'utils/auth'
export default {
  name: 'SupportComponent',
  async beforeRouteEnter(to, from, next) {
    await getConfigs()
    next()
  },
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
