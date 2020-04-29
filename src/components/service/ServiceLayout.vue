<template>
  <div class="remote-wrapper service-wrapper">
    <sub-view></sub-view>

    <transition name="share" mode="out-in">
      <share-list v-if="view === 'drawing'"></share-list>
    </transition>

    <transition name="main" mode="out-in">
      <main-view v-if="view === 'stream'"></main-view>
      <drawing-view v-if="view === 'drawing'"></drawing-view>
      <ar-view v-if="view === 'ar'"></ar-view>
    </transition>

    <video-list :class="{ draw: view === 'drawing' }"></video-list>

    <!-- <component :is="viewComponent"></component> -->
  </div>
</template>

<script>
import ShareList from './drawing/ShareList'
import SubView from './subview/SubView'
import VideoList from './mainview/VideoList'

import { mapGetters } from 'vuex'
export default {
  name: 'ServiceLayout',
  components: {
    ShareList,
    SubView,
    MainView: () => import('./mainview/MainView'),
    DrawingView: () => import('./drawing/Drawing'),
    ArView: () => import('./ar/Ar'),
    VideoList,
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['view']),
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {
    // this.$openvidu.getDevices().then(res => {
    //   console.log(res)
    // })
  },
}
</script>

<style lang="scss" src="assets/style/service.scss"></style>

<style lang="scss">
@import '~assets/style/vars';
.share-enter-active,
.share-leave-active {
  transition: transform ease 0.3s;
}
.share-enter {
  transform: translateX(-#{$share_width});
}
.share-enter-to {
  transform: translateX(0);
}
.share-leave {
  transform: translateX(0);
}
.share-leave-to {
  transform: translateX(-#{$share_width});
}

.main-enter-active,
.main-leave-active {
  transition: opacity ease 0.2s;
}
.main-enter {
  opacity: 0;
}
.main-enter-to {
  opacity: 1;
}
.main-leave {
  opacity: 1;
}
.main-leave-to {
  opacity: 0;
}
</style>
