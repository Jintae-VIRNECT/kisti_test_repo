<template>
  <error-component
    :title="$t('support.spot_already_use')"
    :description="{
      default: $t('support.spot_already_use_description'),
      onpremise: $t('support.spot_already_use_description'),
    }"
    :descriptionMobile="{
      default: $t('support.spot_already_use_description'),
      onpremise: $t('support.spot_already_use_description'),
    }"
  >
    <button
      slot="error-body-button"
      class="error-body--button spot-close-btn"
      @click="closeBrowser"
    >
      {{ $t('support.close_browser') }}
    </button>
  </error-component>
</template>

<script>
import ErrorComponent from '../error/ErrorComponent'
import { spotControlRouterGuard } from 'utils/validator'

export default {
  name: 'SpotErrorComponent',
  components: {
    ErrorComponent,
  },
  //spot 페이지 진입 라우터 가드
  // beforeRouteEnter(to, from, next) {
  //   spotControlRouterGuard(next)
  // },
  methods: {
    closeBrowser() {
      close()
    },
  },
}
</script>
<style lang="scss" scoped>
@import 'assets/style/common';

.spot-close-btn {
  color: #303030;
  font-weight: 500;

  @include mobile {
    display: initial;
  }
}
</style>
