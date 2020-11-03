<template>
  <tab-view>
    <div class="workcard-list">
      <card class="card-skeleton" width="100%" height="27.143em"> </card>
      <card class="card-skeleton" width="100%" height="27.143em"> </card>
      <card class="card-skeleton" width="100%" height="27.143em"> </card>
      <card class="card-skeleton" width="100%" height="27.143em"> </card>
      <card class="card-skeleton" width="100%" height="27.143em"> </card>
    </div>
  </tab-view>
</template>

<script>
import Card from 'Card'
import TabView from '../partials/WorkspaceTabView'
export default {
  components: {
    Card,
    TabView,
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';
@import '~assets/style/mixin';

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
.card.card-skeleton {
  position: relative;
  max-width: 25rem;
  height: 27.143rem;
  padding: 2.286rem;
  overflow: hidden;
  background-color: #313135;
  animation: skeleton-animation 1.5s ease-in-out 0.5s infinite;
}
</style>
