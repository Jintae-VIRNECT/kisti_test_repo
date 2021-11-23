<template>
  <div class="drawing-tools tools">
    <template v-if="isMobileSize">
      <div
        class="mobile-tools-container drawing"
        :class="{ active, leader: isLeader }"
      >
        <undo :disableTooltip="true" :disabled="!toolStatus.undo"></undo>
        <redo :disableTooltip="true" :disabled="!toolStatus.redo"></redo>
        <clear :disableTooltip="true" :disabled="!toolStatus.clear"></clear>
        <clear-all
          v-if="isLeader"
          :disableTooltip="true"
          :disabled="!toolStatus.clearAll"
        ></clear-all>
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
      <undo :disabled="!toolStatus.undo"></undo>
      <redo :disabled="!toolStatus.redo"></redo>
      <clear :disabled="!toolStatus.clear"></clear>
      <clear-all v-if="isLeader" :disabled="!toolStatus.clearAll"></clear-all>
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
import toolStatusMixin from 'mixins/toolStatus'

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
  mixins: [toolStatusMixin],
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
  created() {
    this.activateToolStatusUpdateListener()
  },
  beforeDestroy() {
    this.deactivateToolStatusUpdateListener()
  },
}
</script>
