<template>
  <div class="content-control-popover">
    <header>
      <h5>{{ $t('service.3d_content_control') }}</h5>
      <button class="close-btn" @click="close"></button>
    </header>
    <div class="content-control-popover__body">
      <div class="body-control-wrap">
        <div class="left-control-wrap">
          <div class="move-rotation-switch">
            <button
              class="move-btn"
              :class="{ on: controlPart === AR_3D_CONTROL_TYPE.MOVE }"
              @click="setControlPart(AR_3D_CONTROL_TYPE.MOVE)"
            ></button>
            <button
              class="rotate-btn"
              :class="{ on: controlPart === AR_3D_CONTROL_TYPE.ROTATE }"
              @click="setControlPart(AR_3D_CONTROL_TYPE.ROTATE)"
            ></button>
          </div>
          <button class="reset-btn" @click="resetControl"></button>
        </div>
        <div
          class="right-control-wrap"
          :class="{ rotate: controlPart === AR_3D_CONTROL_TYPE.ROTATE }"
        >
          <div
            v-if="controlPart === AR_3D_CONTROL_TYPE.MOVE"
            class="x-y-btn-wrap"
          >
            <button class="top" @click="setControl(1, AXIS.Y)"></button>
            <button class="left" @click="setControl(-1, AXIS.X)"></button>
            <button class="bottom" @click="setControl(-1, AXIS.Y)"></button>
            <button class="right" @click="setControl(1, AXIS.X)"></button>
          </div>
          <div v-else class="x-y-btn-wrap">
            <button class="top" @click="setControl(-1, AXIS.X)"></button>
            <button class="left" @click="setControl(-1, AXIS.Y)"></button>
            <button class="bottom" @click="setControl(1, AXIS.X)"></button>
            <button class="right" @click="setControl(1, AXIS.Y)"></button>
          </div>
          <div
            v-if="controlPart === AR_3D_CONTROL_TYPE.MOVE"
            class="z-btn-wrap"
          >
            <button class="top" @click="setControl(-1, AXIS.Z)"></button>
            <button class="bottom" @click="setControl(1, AXIS.Z)"></button>
          </div>
          <div v-else class="z-btn-wrap">
            <button class="top" @click="setControl(1, AXIS.Z)"></button>
            <button class="bottom" @click="setControl(-1, AXIS.Z)"></button>
          </div>
        </div>
      </div>
      <div class="zoom-control">
        <img class="icon-img" alt="icon-img" />
        <input
          class="zoom-range"
          type="range"
          v-model="zoomValue"
          min="50"
          max="200"
        />
        <p class="zoom-value">{{ zoomValue }}%</p>
      </div>
    </div>
  </div>
</template>

<script>
import ar3dContentControlMixin from 'mixins/ar3dContentControl'

export default {
  name: 'Control3dPopOver',
  mixins: [ar3dContentControlMixin],
  props: {
    visible: {
      type: [Boolean, Object],
      default: false,
    },
  },
  data() {
    return {
      visibleFlag: false,
    }
  },
  watch: {
    visible(flag) {
      this.visibleFlag = !!flag
    },
  },
  methods: {
    close() {
      this.$emit('close')
    },
  },
}
</script>

<style
  lang="scss"
  src="assets/style/service/service-control-popover.scss"
></style>
