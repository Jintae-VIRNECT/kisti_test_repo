<template>
  <div class="ar-tools tools">
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
      active: 'pointing',
      isRecording: false,
      EXPERT_LEADER: ROLE.EXPERT_LEADER,
      leaderDrawing: false,
    }
  },
  computed: {
    ...mapGetters(['viewAction']),
    isLeader() {
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        return true
      } else {
        return false
      }
    },
    canDrawing() {
      if (
        this.account.roleType === ROLE.EXPERT_LEADER &&
        this.viewAction === ACTION.AR_DRAWING
      ) {
        return true
      } else {
        return false
      }
    },
    canPointing() {
      if (this.viewAction === ACTION.AR_POINTING) {
        if (this.account.roleType === ROLE.EXPERT_LEADER) {
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
