<template>
  <div class="error">
    <div class="error-body">
      <div class="error-body__graphic">
        <!-- pc or mobile image-->
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
      <!-- title -->
      <h2 class="error-body__title" v-html="title"></h2>
      <!-- description -->
      <p
        class="error-body--description"
        v-html="isOnpremise ? description.onpremise : description.default"
      ></p>
      <!-- description for mobile -->
      <p
        class="error-body--mobile"
        v-html="
          isOnpremise ? descriptionMobile.onpremise : descriptionMobile.default
        "
      ></p>
      <!-- 버튼 -->
      <div class="error-body-button__container">
        <slot name="error-body-button"></slot>
      </div>
    </div>

    <footer class="error-footer" v-if="!isOnpremise">
      <div class="error-footer__inner">
        <p class="error-footer__copyright">
          &copy; VIRNECT CO., LTD. All rights reserved.
        </p>
      </div>
    </footer>
  </div>
</template>

<script>
//global mixin : isSCreenDesktop, isOnpremise

export default {
  name: 'ErrorComponent',
  props: {
    title: {
      type: String,
      default: function() {
        return this.$t('support.header_title')
      },
    },
    description: {
      type: Object,
      default: function() {
        return {
          onpremise: this.$t('support.pc_web'),
          default: this.$t('support.header_description'),
        }
      },
    },
    descriptionMobile: {
      type: Object,
      default: function() {
        return {
          onpremise: this.$t('support.pc_web'),
          default: this.$t('support.header_description_mobile'),
        }
      },
    },
  },
  methods: {},
}
</script>
<style lang="scss" src="assets/style/error.scss"></style>
