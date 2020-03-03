<template>
  <div class="service-wrapper">
    <sub-view></sub-view>

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
import SubView from './subview/SubView'
import { mapGetters } from 'vuex'
export default {
  name: 'CallLayout',
  components: {
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
    this.$openvidu.getDevices().then(res => {
      console.log(res)
    })
  },
}
</script>
