<template>
  <div>
    <div id="dim" v-if="isDim && !isMobile" @click="gnbClose"></div>
    <ul id="GNB">
      <li
        v-for="(gnb, idx) of customGnb"
        :key="idx"
        class="nav-item"
        :class="{
          vertical: isVertical(gnb.text),
          active:
            idx === activeMenu ||
            $route.path == gnb.page ||
            $route.path == gnb.to,
        }"
      >
        <button
          @click="
            chnActive(idx)
            chnRoute(gnb.to, true)
          "
        >
          {{ gnb.text }}
        </button>
        <DepthNavigation
          v-if="isShow && checkShow(idx)"
          :gnb="gnb"
          @chnRoute="chnRoute"
        />
      </li>
    </ul>
  </div>
</template>
<script>
import DepthNavigation from './DepthNavigation.vue'
export default {
  components: { DepthNavigation },
  props: {
    isMobile: Boolean,
    type: String,
    customGnb: Array,
  },
  data() {
    return {
      isAccordian: false,
      activeMenu: null,
      isDim: false,
    }
  },
  watch: {
    $route() {
      if (this.$route.path === '/') {
        this.activeMenu = null
      }
    },
  },
  computed: {
    isShow() {
      if (this.isAccordian && this.type === 'promote') {
        return true
      } else return false
    },
  },
  methods: {
    gnbClose() {
      this.isAccordian = false
      this.isDim = false
    },
    checkShow(idx) {
      return idx === this.activeMenu
    },
    isVertical(name) {
      if (/Pricing|Resolurces|Company/.test(name)) {
        return true
      }
    },
    accordianToggle(before, after) {
      if (!this.isAccordian) {
        this.isDim = true
        this.isAccordian = true
      } else {
        if (before === after) {
          this.isAccordian = false
          this.activeMenu = null
        }
      }
    },
    chnActive(idx) {
      const beforeIdx = this.activeMenu
      this.accordianToggle(beforeIdx, idx)
      this.activeMenu = idx
    },
    chnRoute(route, depthNav) {
      if (depthNav && this.type === 'promote') {
        return
      } else if (this.$route.path === route) return
      this.$router.push({
        path: route,
      })
      if (this.isShow) {
        this.isAccordian = false
        this.isDim = false
      }
    },
  },
}
</script>
