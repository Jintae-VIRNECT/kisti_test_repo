<template>
  <footer class="mobile-service-footer">
    <div class="footer-tab-container">
      <button
        v-for="menu in tabMenus"
        :class="{ active: view === menu.key, notice: menu.notice }"
        :key="menu.key"
        @click="goTab(menu.key)"
      >
        <span class="layer"></span>
        {{ $t(menu.text) }}
      </button>
    </div>
    <div class="footer-button-container">
      <mobile-more-button></mobile-more-button>
      <mobile-capture-button></mobile-capture-button>
      <mobile-flash-button></mobile-flash-button>
    </div>
  </footer>
</template>

<script>
import tabChangeMixin from 'mixins/tabChange'
import MobileMoreButton from './partials/MobileMoreButton.vue'
import MobileCaptureButton from './partials/MobileCaptureButton.vue'
import MobileFlashButton from './partials/MobileFlashButton.vue'

export default {
  mixins: [tabChangeMixin],
  components: {
    MobileMoreButton,
    MobileCaptureButton,
    MobileFlashButton,
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-service-footer {
  position: absolute;
  bottom: 2.8rem;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;

  .footer-tab-container {
    //width: 25.8rem;
    margin-bottom: 1.6rem;

    > button {
      position: relative;
      width: 8.7rem;
      //height: 2.8rem;
      margin: 0 0.3rem;
      padding: 0.4rem;
      color: $new_color_text_main;
      border-radius: 1.4rem;
      @include fontLevel(75);

      &.active {
        background-color: rgba($new_color_bg_tab_active_rgb, 0.5);
      }
      &.notice::after {
        position: absolute;
        top: 2px;
        right: 10px;
        width: 0.6rem;
        height: 0.6rem;
        background-color: #d9333a;
        border-radius: 50%;
        content: '';
      }
    }
  }

  .footer-button-container {
    display: flex;
  }
}
</style>
