<template>
  <div v-if="toolVisible" class="ar-tools tools">
    <!-- mobile -->
    <template v-if="isMobileSize">
      <div
        class="mobile-tools-container ar"
        :class="{ 'ar-3d': is3dContentsMode, active: toolbarActive }"
      >
        <undo
          v-if="!is3dContentsMode"
          :disableTooltip="true"
          :disabled="!canDrawingOrPointing || !toolStatus.undo"
        ></undo>
        <redo
          v-if="!is3dContentsMode"
          :disableTooltip="true"
          :disabled="!canDrawingOrPointing || !toolStatus.redo"
        ></redo>
        <clear
          :disableTooltip="true"
          :disabled="
            (!canDrawingOrPointing && !is3dContentClearble) || !toolStatus.clear
          "
        ></clear>
        <div v-if="!is3dContentsMode" class="division"></div>
        <color
          v-if="!is3dContentsMode"
          :disabled="!canDrawingOrPointing"
        ></color>
      </div>
      <button
        class="tools-toggle-btn"
        :class="{ active: toolbarActive }"
        @click="toggle"
      >
        <img src="~assets/image/call/icn_dropdown_new.svg" alt="dropdown" />
      </button>

      <ar-capture
        v-if="
          viewAction !== ACTION.AR_DRAWING && isLeader && !isMainViewHololens
        "
        :disableTooltip="true"
        class="mobile-ar-tools-btn"
        :disabled="toolDeactivated || isMainViewHololens"
      ></ar-capture>
      <ar-pointing
        v-if="viewAction !== ACTION.AR_POINTING"
        :disableTooltip="true"
        :disabled="leaderDrawing || toolDeactivated"
        class="mobile-ar-tools-btn"
      ></ar-pointing>
      <ar-3d-content
        v-if="!is3dContentsMode && isLeader"
        :disableTooltip="true"
        class="mobile-ar-tools-btn"
        :disabled="toolDeactivated"
      ></ar-3d-content>
    </template>
    <!-- pc -->
    <template v-else>
      <ar-pointing :disabled="leaderDrawing || toolDeactivated"></ar-pointing>
      <template v-if="isLeader">
        <ar-3d-content :disabled="toolDeactivated"></ar-3d-content>
        <ar-capture
          :disabled="toolDeactivated || isMainViewHololens"
        ></ar-capture>
        <div class="division"></div>
        <line-mode :disabled="!canDrawing || isMainViewHololens"></line-mode>
        <line-width :disabled="!canDrawing || isMainViewHololens"></line-width>
      </template>
      <color :disabled="!canDrawingOrPointing"></color>
      <div class="division"></div>
      <undo :disabled="!canDrawingOrPointing || !toolStatus.undo"></undo>
      <redo :disabled="!canDrawingOrPointing || !toolStatus.redo"></redo>
      <clear
        :disabled="
          (!canDrawingOrPointing && !is3dContentClearble) || !toolStatus.clear
        "
      ></clear>
    </template>
  </div>
</template>

<script>
import {
  ArPointing,
  ArCapture,
  Ar3dContent,
  LineMode,
  LineWidth,
  Color,
  Undo,
  Redo,
  Clear,
} from './partials'
import { mapGetters } from 'vuex'
import { ACTION } from 'configs/view.config'
import { ROLE, AR_3D_FILE_SHARE_STATUS } from 'configs/remote.config'
import { DEVICE } from 'configs/device.config'
import toolStatusMixin from 'mixins/toolStatus'

export default {
  name: 'ArTools',
  components: {
    ArPointing,
    ArCapture,
    Ar3dContent,
    LineMode,
    LineWidth,
    Color,
    Undo,
    Redo,
    Clear,
  },
  mixins: [toolStatusMixin],
  data() {
    return {
      toolVisible: false,
      toolbarActive: false,
      active: 'pointing',
      isRecording: false,
      LEADER: ROLE.LEADER,
      leaderDrawing: false,
      ACTION: Object.freeze(ACTION),
    }
  },
  computed: {
    ...mapGetters([
      'mainView',
      'viewAction',
      'ar3dShareStatus',
      'share3dContent',
    ]),
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
    canDrawingOrPointing() {
      return this.canDrawing || this.canPointing
    },
    is3dContentsMode() {
      if (this.viewAction === ACTION.AR_3D) return true
      return false
    },
    is3dContentClearble() {
      if (
        this.is3dContentsMode &&
        this.ar3dShareStatus === AR_3D_FILE_SHARE_STATUS.COMPLETE &&
        this.share3dContent.objectName
      ) {
        return true
      } else return false
    },
    toolDeactivated() {
      if (
        this.is3dContentsMode &&
        this.ar3dShareStatus === AR_3D_FILE_SHARE_STATUS.START
      )
        return true
      else return false
    },
    isMainViewHololens() {
      if (this.mainView.deviceType === DEVICE.HOLOLENS) return true
      else return false
    },
  },
  watch: {
    viewAction(val) {
      if ([ACTION.AR_POINTING, ACTION.AR_DRAWING, ACTION.AR_3D].includes(val)) {
        this.setUndoAvailable(false)
        this.setRedoAvailable(false)
        this.setClearAvailable(false)
      }
      if (val === ACTION.AR_3D && !this.isLeader) {
        this.toolVisible = false
      } else {
        this.toolVisible = true
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

    this.activateToolStatusUpdateListener()
  },
  beforeDestroy() {
    if (!this.isLeader) {
      this.$eventBus.$off('leaderDrawing', this.setDrawing)
    }

    this.deactivateToolStatusUpdateListener()
  },
}
</script>
