<template>
  <popover
    placement="top-start"
    width="18.8rem"
    trigger="click"
    popperClass="popover-more"
    :useTopMargin="-10"
    @visible="setVisible"
  >
    <button
      slot="reference"
      class="mobile-more-btn"
      :class="{ visible }"
    ></button>
    <div>
      <!-- <button>이전</button> -->
      <div class="popover-profile__tools">
        <stream size="4rem"></stream>
        <mic size="4rem"></mic>
        <speaker size="4rem"></speaker>
      </div>
      <div>
        <ul>
          <li class="popover-more__camera-control">
            <img src="" alt="" />
            {{ $t('service.camera_control') }}
          </li>
          <li class="popover-more__server-record">
            <img src="" alt="" />
            {{ $t('service.record_server') }}
          </li>
          <li class="popover-more__participant">
            <img src="" alt="" />
            {{ $t('workspace.info_remote_member') }}
          </li>
          <li class="popover-more__spot-control">
            <img src="" alt="" />
            {{ $t('service.spot_control') }}
          </li>
        </ul>
      </div>
    </div>
  </popover>
</template>

<script>
import Popover from 'Popover'
import Stream from '../../../header/tools/Stream.vue'
import Mic from '../../../header/tools/Mic'
import Speaker from '../../../header/tools/Speaker'

export default {
  components: {
    Popover,
    Stream,
    Mic,
    Speaker,
  },
  data() {
    return {
      visible: false,
    }
  },
  methods: {
    setVisible(visible) {
      this.visible = visible
    },
  },
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';

.mobile-more-btn {
  width: 6rem;
  height: 6rem;
  margin: 0 1rem;
  background: url(~assets/image/call/mdpi_icon_more_new.svg) center/2.4rem
    no-repeat;
  background-color: $new_color_bg_button_primary;
  border-radius: 50%;
  transition: transform 0.3s;

  &.visible {
    transform: rotate(45deg);
  }
}
</style>

<style lang="scss">
@import '~assets/style/mixin';

.popover-more {
  background-color: $new_color_popup_bg;
}
</style>
