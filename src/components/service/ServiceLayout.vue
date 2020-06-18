<template>
  <section class="remote-layout">
    <header-section></header-section>
    <div class="remote-wrapper service-wrapper">
      <sub-view></sub-view>

      <transition name="share">
        <share v-if="isExpert && view === 'drawing'"></share>
      </transition>

      <main
        class="main-wrapper"
        :class="{ shareview: isExpert && view === 'drawing' }"
      >
        <transition name="main">
          <stream-view v-show="view === 'stream'"></stream-view>
        </transition>
        <transition name="main">
          <drawing-view v-show="view === 'drawing'"></drawing-view>
        </transition>
        <transition name="main">
          <ar-view v-show="view === 'ar'"></ar-view>
        </transition>
      </main>

      <user-list
        :class="{
          shareview: isExpert && view === 'drawing',
        }"
      ></user-list>

      <!-- <component :is="viewComponent"></component> -->
    </div>
  </section>
</template>

<script>
import HeaderSection from 'components/header/Header'
import SubView from './subview/SubView'
import UserList from './participants/ParticipantList'
import { ROLE } from 'configs/remote.config'

import { mapGetters } from 'vuex'
export default {
  name: 'ServiceLayout',
  beforeRouteEnter(to, from, next) {
    next(vm => {
      vm.$store.dispatch('callReset')
    })
  },
  beforeRouteLeave(to, from, next) {
    next(vm => {
      vm.$store.commit('clear')
    })
  },
  components: {
    HeaderSection,
    SubView,
    UserList,
    StreamView: () => import('./ServiceStream'),
    DrawingView: () => import('./ServiceDrawing'),
    ArView: () => import('./ServiceAr'),
    Share: () => import('./share/Share'),
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['view']),
    isExpert() {
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        return true
      } else {
        return false
      }
    },
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  created() {
    window.onbeforeunload = () => {
      return true
    }
  },
  beforeDestroy() {
    window.onbeforeunload = () => {}
  },
  mounted() {
    // this.$call.getDevices().then(res => {
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

.main-enter-active {
  transition: opacity ease 0.2s 0.2s;
}
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
