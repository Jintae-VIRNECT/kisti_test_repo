<template>
  <section class="remote-layout">
    <header-section></header-section>
    <div class="remote-wrapper service-wrapper">
      <sub-view></sub-view>

      <transition name="share">
        <share v-show="isExpert && currentView === 'drawing'"></share>
      </transition>

      <main
        class="main-wrapper"
        :class="{ shareview: isExpert && currentView === 'drawing' }"
      >
        <transition name="main">
          <stream-view
            :class="{ hide: currentView !== 'stream' }"
          ></stream-view>
        </transition>
        <transition name="main">
          <drawing-view v-show="currentView === 'drawing'"></drawing-view>
        </transition>
        <transition name="main">
          <ar-view v-show="currentView === 'ar'"></ar-view>
        </transition>
        <transition name="popover">
          <capture-modal
            v-if="captureFile.id"
            :file="captureFile"
          ></capture-modal>
        </transition>
      </main>

      <user-list
        :class="{
          shareview: isExpert && currentView === 'drawing',
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
import CaptureModal from './modal/CaptureModal'
import { ROLE } from 'configs/remote.config'
import { VIEW } from 'configs/view.config'

import { mapGetters } from 'vuex'
export default {
  name: 'ServiceLayout',
  beforeRouteEnter(to, from, next) {
    if (from.name !== 'workspace') {
      next({ name: 'workspace' })
    }
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
    CaptureModal,
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['view', 'captureFile']),
    isExpert() {
      if (this.account.roleType === ROLE.EXPERT_LEADER) {
        return true
      } else {
        return false
      }
    },
    currentView() {
      if (this.view === VIEW.STREAM) {
        return 'stream'
      } else if (this.view === VIEW.DRAWING) {
        return 'drawing'
      } else if (this.view === VIEW.AR) {
        return 'ar'
      }
      return ''
    },
  },

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
  async mounted() {
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
