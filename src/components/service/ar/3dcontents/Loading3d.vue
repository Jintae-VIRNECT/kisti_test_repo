<template>
  <!-- 3d 로딩 -->
  <div class="ar-3d-content__loading">
    <div class="ar-3d-content__loading-inner">
      <div class="edge left top"></div>
      <div class="edge left bottom"></div>
      <div class="edge right top"></div>
      <div class="edge right bottom"></div>
      <img :src="loadingImg" alt="loading" />
      <h1>{{ $t('service.ar_3d_loading_header_title') }}</h1>
      <p class="loading-title" v-html="$t('service.ar_3d_loading_title')"></p>
    </div>
  </div>
</template>

<script>
export default {
  computed: {
    loadingImg() {
      if (this.isMobileSize)
        return require('assets/image/call/ic_ar_3d_loading_spinner_mobile.svg')
      else return require('assets/image/call/ic_ar_3d_loading_spinner.svg')
    },
  },
}
</script>

<style></style>
