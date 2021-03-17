<template>
  <div>
    <pano-video
      videoElementId="main-video"
      targetRef="mainVideo"
      :connectionId="mainView.connectionId"
      type="viewer"
    ></pano-video>
  </div>
</template>

<script>
import PanoVideo from 'PanoVideo'
import { mapGetters } from 'vuex'

export default {
  name: 'MovingViewer',
  components: {
    PanoVideo,
  },

  computed: {
    ...mapGetters(['mainView']),
  },
}
</script>
<style lang="scss">
.main-video__moving-viewer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  max-width: 100%;
  height: 100%;
  max-height: 100%;
}
</style>
