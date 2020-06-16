<template>
  <section class="remote-layout">
    <header-section></header-section>
    <div class="remote-wrapper service-wrapper">
      <sub-view></sub-view>

      <transition name="share" mode="out-in">
        <share
          v-if="account.roleType === EXPERT_LEADER && view === 'drawing'"
        ></share>
      </transition>

      <transition name="main" mode="out-in">
        <stream-view v-if="view === 'stream'"></stream-view>
        <drawing-view v-if="view === 'drawing'"></drawing-view>
        <ar-view v-if="view === 'ar'"></ar-view>
      </transition>

      <user-list
        :class="{
          shareview: account.roleType === EXPERT_LEADER && view === 'drawing',
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
    return {
      EXPERT_LEADER: ROLE.EXPERT_LEADER,
    }
  },
  computed: {
    ...mapGetters(['view']),
  },
  watch: {},
  methods: {},

  /* Lifecycles */
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
