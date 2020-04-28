<template>
  <div class="remote-wrapper service-wrapper">
    <sub-view></sub-view>

    <transition name="share" mode="out-in">
      <share-list v-if="view === 'drawing'"></share-list>
    </transition>

    <transition>
      <main-view v-show="view === 'stream'"></main-view>
    </transition>

    <transition>
      <drawing-view v-show="view === 'drawing'"></drawing-view>
    </transition>

    <transition>
      <ar-view v-show="view === 'ar'"></ar-view>
    </transition>

    <!-- <component :is="viewComponent"></component> -->
  </div>
</template>

<script>
import ShareList from './drawing/ShareList'
import SubView from './subview/SubView'
import { mapGetters } from 'vuex'
export default {
  name: 'ServiceLayout',
  components: {
    ShareList,
    SubView,
    MainView: () => import('./mainview/MainView'),
    DrawingView: () => import('./drawing/Drawing'),
    ArView: () => import('./ar/Ar'),
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
.share-enter-active {
  transition: transform ease 0.4s;
}
.share-enter {
  transform: translateX(-#{$share_width});
}
.share-enter-to {
  transform: translateX(0);
}
.share-leave-active {
  transition: transform ease 0.4s;
}
.share-leave {
  transform: translateX(0);
}
.share-leave-to {
  transform: translateX(-#{$share_width});
}
</style>
