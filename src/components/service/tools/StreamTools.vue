<template>
  <div class="stream-tools tools">
    <pointing></pointing>
    <color></color>
    <template v-if="!isTablet && !isSafari">
      <div class="division"></div>
      <screen-share></screen-share>
    </template>
  </div>
</template>

<script>
import { Pointing, Color, ScreenShare } from './partials'

export default {
  name: 'StreamTools',
  components: {
    Pointing,
    Color,
    ScreenShare,
  },
}
</script>
