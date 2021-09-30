<template>
  <tab-view title="   " customClass="skeleton-view">
    <div class="skeleton-screen">
      <!-- <card class="card-skeleton" width="100%" height="27.143em"> </card>
      <card class="card-skeleton" width="100%" height="27.143em"> </card>
      <card class="card-skeleton" width="100%" height="27.143em"> </card>
      <card class="card-skeleton" width="100%" height="27.143em"> </card>
      <card class="card-skeleton" width="100%" height="27.143em"> </card> -->
    </div>
  </tab-view>
</template>

<script>
import TabView from '../partials/WorkspaceTabView'
export default {
  components: {
    TabView,
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';
@import '~assets/style/mixin';

.tab-view.skeleton-view {
  position: absolute;
  top: 4.857rem;
  bottom: 0;
  left: 0;
  width: 100%;
}

@keyframes skeleton-animation {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.4;
  }
  100% {
    opacity: 1;
  }
}

.skeleton-animation {
  animation-name: skeleton-animation;
  animation-duration: 1.5s;
  animation-play-state: running;
  animation-timing-function: ease-in-out;
  animation-delay: 0.5s;
  animation-iteration-count: infinite;
  animation-direction: normal;
  animation-fill-mode: none;
}
.skeleton-screen {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: #313135;
  animation: skeleton-animation 1.5s ease-in-out 0.5s infinite;
}

@include responsive-mobile {
  .skeleton-screen {
    background-color: $new_color_bg_sub;
  }
}
</style>
