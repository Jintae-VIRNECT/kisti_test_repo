<template>
  <div class="drawing-tools tools">
    <template v-if="isMobileSize">
      <div
        class="mobile-tools-container drawing"
        :class="{ active, leader: isLeader }"
      >
        <undo :disableTooltip="true"></undo>
        <redo :disableTooltip="true"></redo>
        <clear :disableTooltip="true"></clear>
        <clear-all v-if="isLeader" :disableTooltip="true"></clear-all>
        <div class="division"></div>
        <color :disableTooltip="true"></color>
        <drawing-lock @click="onDrawingLock"></drawing-lock>
      </div>
      <button class="tools-toggle-btn" :class="{ active }" @click="toggle">
        <img src="~assets/image/call/icn_dropdown_new.svg" alt="dropdown" />
      </button>
    </template>
    <template v-else>
      <line-mode></line-mode>
      <line-width></line-width>
      <text-mode></text-mode>
      <text-size></text-size>
      <color></color>
      <div class="division"></div>
      <undo></undo>
      <redo></redo>
      <clear></clear>
      <clear-all v-if="isLeader"></clear-all>
    </template>
  </div>
</template>

<script>
import {
  LineMode,
  TextMode,
  Color,
  LineWidth,
  TextSize,
  Undo,
  Redo,
  Clear,
  ClearAll,
  DrawingLock,
} from './partials'
import { ROLE } from 'configs/remote.config'
import { mapActions } from 'vuex'
import { ACTION } from 'configs/view.config'

export default {
  name: 'DrawingTools',
  components: {
    LineMode,
    TextMode,
    Color,
    LineWidth,
    TextSize,
    Undo,
    Redo,
    Clear,
    ClearAll,
    DrawingLock,
  },
  data() {
    return {
      active: false,
      DRAWING_LOCK: Object.freeze(ACTION.DRAWING_LOCK),
      DRAWING_LINE: Object.freeze(ACTION.DRAWING_LINE),
    }
  },
  computed: {
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
  watch: {},
  methods: {
    ...mapActions(['setAction']),
    toggle() {
      this.active = !this.active
      if (this.active) {
        this.setAction(this.DRAWING_LINE)
      }
    },
    onDrawingLock() {
      this.active = false
      this.setAction(this.DRAWING_LOCK)
    },
  },

  /* Lifecycles */
  beforeDestroy() {},
  mounted() {},
}
</script>
