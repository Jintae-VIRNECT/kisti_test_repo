<template>
  <div class="mobile-drawing-tools tools">
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
} from './partials'
import { ROLE } from 'configs/remote.config'

export default {
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
  },
  data() {
    return {}
  },
  computed: {
    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },
}
</script>

<style></style>
