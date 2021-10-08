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
      <template v-if="view === VIEW.STREAM">
        <mobile-more-button
          @selectMember="openParticipantModal"
        ></mobile-more-button>
        <mobile-capture-button
          :disabled="!isMainViewOn"
        ></mobile-capture-button>
        <mobile-flash-button></mobile-flash-button>
      </template>
      <template v-else-if="view === VIEW.DRAWING">
        <mobile-drawing-exit-button></mobile-drawing-exit-button>
        <mobile-file-list-button></mobile-file-list-button>
        <mobile-download-button></mobile-download-button>
        <mobile-upload-button></mobile-upload-button>
      </template>
    </div>
    <mobile-participant-modal
      :visible.sync="isParticipantModalShow"
      :beforeClose="beforeClose"
    ></mobile-participant-modal>
  </footer>
</template>

<script>
import tabChangeMixin from 'mixins/tabChange'
import MobileMoreButton from './partials/MobileMoreButton'
import MobileCaptureButton from './partials/MobileCaptureButton'
import MobileFlashButton from './partials/MobileFlashButton'
import MobileUploadButton from './partials/MobileUploadButton'
import MobileFileListButton from './partials/MobileFileListButton'
import MobileDownloadButton from './partials/MobileDownloadButton'
import MobileDrawingExitButton from './partials/MobileDrawingExitButton'
//import MobileParticipantModal from '../modal/MobileParticipantModal'
import { mapGetters } from 'vuex'
import { VIEW } from 'configs/view.config'

export default {
  mixins: [tabChangeMixin],
  components: {
    MobileMoreButton,
    MobileCaptureButton,
    MobileFlashButton,
    MobileParticipantModal: () => import('../modal/MobileParticipantModal'),
    MobileUploadButton,
    MobileFileListButton,
    MobileDownloadButton,
    MobileDrawingExitButton,
  },
  data() {
    return {
      VIEW: Object.freeze(VIEW),
      isParticipantModalShow: false,
    }
  },
  computed: {
    ...mapGetters(['mainView']),
    isMainViewOn() {
      return this.mainView && this.mainView.id && this.mainView.video
    },
  },
  methods: {
    openParticipantModal() {
      this.isParticipantModalShow = true
    },
    beforeClose() {
      this.isParticipantModalShow = false
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-service-footer {
  position: absolute;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding-bottom: 2.8rem;
  background: linear-gradient(rgba(0, 0, 0, 0), rgba(0, 0, 0, 0.4));

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
    height: 6rem;
  }
}
</style>
