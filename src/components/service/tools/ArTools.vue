<template>
  <div class="ar-tools tools">
    <template v-if="isMobileSize">
      <div class="mobile-tools-container ar" :class="{ active: toolbarActive }">
        <undo
          :disableTooltip="true"
          :disabled="!(canDrawing || canPointing)"
        ></undo>
        <redo
          :disableTooltip="true"
          :disabled="!(canDrawing || canPointing)"
        ></redo>
        <clear
          :disableTooltip="true"
          :disabled="!(canDrawing || canPointing)"
        ></clear>
        <div class="division"></div>
        <color :disabled="!(canDrawing || canPointing)"></color>
      </div>
      <button
        class="tools-toggle-btn"
        :class="{ active: toolbarActive }"
        @click="toggle"
      >
        <img src="~assets/image/call/icn_dropdown_new.svg" alt="dropdown" />
      </button>

      <ar-capture
        v-if="viewAction !== ACTION.AR_DRAWING"
        :disableTooltip="true"
        class="mobile-ar-tools-btn"
      ></ar-capture>
      <ar-pointing
        :disableTooltip="true"
        class="mobile-ar-tools-btn"
        v-if="viewAction === ACTION.AR_DRAWING"
        :disabled="leaderDrawing"
      ></ar-pointing>
    </template>
    <template v-else>
      <ar-pointing :disabled="leaderDrawing"></ar-pointing>
      <template v-if="isLeader">
        <ar-capture></ar-capture>
        <div class="division"></div>
        <line-mode :disabled="!canDrawing"></line-mode>
        <line-width :disabled="!canDrawing"></line-width>
      </template>
      <color :disabled="!(canDrawing || canPointing)"></color>
      <div class="division"></div>
      <undo :disabled="!(canDrawing || canPointing)"></undo>
      <redo :disabled="!(canDrawing || canPointing)"></redo>
      <clear :disabled="!(canDrawing || canPointing)"></clear>
    </template>
  </div>
</template>

<script>
import {
  ArPointing,
  ArCapture,
  LineMode,
  LineWidth,
  Color,
  Undo,
  Redo,
  Clear,
} from './partials'
import { mapGetters } from 'vuex'
import { ACTION } from 'configs/view.config'
import { ROLE } from 'configs/remote.config'

export default {
  name: 'ArTools',
  components: {
    ArPointing,
    ArCapture,
    LineMode,
    LineWidth,
    Color,
    Undo,
    Redo,
    Clear,
  },
  data() {
    return {
      toolbarActive: false,
      active: 'pointing',
      isRecording: false,
      LEADER: ROLE.LEADER,
      leaderDrawing: false,
      ACTION: Object.freeze(ACTION),
    }
  },
  computed: {
    ...mapGetters(['viewAction']),
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
    canDrawing() {
      return (
        this.account.roleType === ROLE.LEADER &&
        this.viewAction === ACTION.AR_DRAWING
      )
    },
    canPointing() {
      if (this.viewAction === ACTION.AR_POINTING) {
        if (this.account.roleType === ROLE.LEADER) {
          return true
        }
        if (!this.leaderDrawing) {
          return true
        } else {
          return false
        }
      } else {
        return false
      }
    },
  },
  methods: {
    setDrawing(val) {
      this.leaderDrawing = val
    },
    toggle() {
      this.toolbarActive = !this.toolbarActive
    },
  },

  /* Lifecycles */
  created() {
    if (!this.isLeader) {
      this.$eventBus.$on('leaderDrawing', this.setDrawing)
    }
  },
  beforeDestroy() {
    if (!this.isLeader) {
      this.$eventBus.$off('leaderDrawing', this.setDrawing)
    }
  },
}
</script>
