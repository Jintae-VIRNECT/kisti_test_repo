<template>
  <transition name="fade">
    <article class="loading" v-if="loading">
      <i class="loading-wave-dots"></i>
    </article>
  </transition>
</template>
<script>
export default {
  name: 'loader',
  props: {
    loading: {
      type: Boolean,
      default: false,
    },
  },
}
</script>

<style lang="scss" scoped>
.loading {
  z-index: 9;
  padding: 2.143rem 0 0;
  text-align: center;
}
.loading-wave-dots {
  position: relative;
  &:before {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 0.571rem;
    height: 0.571rem;
    margin-top: -0.286rem;
    margin-left: -0.286rem;
    background-color: #ddd;
    border-radius: 50%;
    -webkit-animation: linear loading-wave-dots 2.8s infinite;
    animation: linear loading-wave-dots 2.8s infinite;
    content: '';
  }
}
@-webkit-keyframes loading-wave-dots {
  0% {
    box-shadow: -2.286rem 0 0 #ddd, -1.143rem 0 0 #ddd, 1.143rem 0 0 #ddd,
      2.286rem 0 0 #ddd;
  }
  5% {
    box-shadow: -2.286rem -0.286rem 0 #ddd, -1.143rem 0 0 #ddd,
      1.143rem 0 0 #ddd, 2.286rem 0 0 #ddd;
    -webkit-transform: translateY(0);
    transform: translateY(0);
  }
  10% {
    box-shadow: -2.286rem -0.429rem 0 #999, -1.143rem -0.286rem 0 #ddd,
      1.143rem 0 0 #ddd, 2.286rem 0 0 #ddd;
    -webkit-transform: translateY(0);
    transform: translateY(0);
  }
  15% {
    background-color: #ddd;
    box-shadow: -2.286rem 0.143rem 0 #ddd, -1.143rem -0.143rem 0 #999,
      1.143rem 0.286rem 0 #ddd, 2.286rem 0.286rem 0 #ddd;
    -webkit-transform: translateY(-0.286rem);
    transform: translateY(-0.286rem);
  }
  20% {
    background-color: #999;
    box-shadow: -2.286rem 0.429rem 0 #ddd, -1.143rem 0.286rem 0 #ddd,
      1.143rem 0.143rem 0 #ddd, 2.286rem 0.429rem 0 #ddd;
    -webkit-transform: translateY(-0.429rem);
    transform: translateY(-0.429rem);
  }
  25% {
    background-color: #ddd;
    box-shadow: -2.286rem 0.143rem 0 #ddd, -1.143rem 0.143rem 0 #ddd,
      1.143rem -0.286rem 0 #999, 2.286rem -0.143rem 0 #ddd;
    -webkit-transform: translateY(-0.143rem);
    transform: translateY(-0.143rem);
  }
  30% {
    box-shadow: -2.286rem 0 0 #ddd, -1.143rem 0 0 #ddd,
      1.143rem -0.143rem 0 #ddd, 2.286rem -0.429rem 0 #999;
    -webkit-transform: translateY(0);
    transform: translateY(0);
  }
  35% {
    box-shadow: -2.286rem 0 0 #ddd, -1.143rem 0 0 #ddd, 1.143rem 0 0 #ddd,
      2.286rem -0.143rem 0 #ddd;
  }
  40% {
    box-shadow: -2.286rem 0 0 #ddd, -1.143rem 0 0 #ddd, 1.143rem 0 0 #ddd,
      2.286rem 0 0 #ddd;
  }
  to {
    box-shadow: -2.286rem 0 0 #ddd, -1.143rem 0 0 #ddd, 1.143rem 0 0 #ddd,
      2.286rem 0 0 #ddd;
  }
}
@keyframes loading-wave-dots {
  0% {
    box-shadow: -2.286rem 0 0 #ddd, -1.143rem 0 0 #ddd, 1.143rem 0 0 #ddd,
      2.286rem 0 0 #ddd;
  }
  5% {
    box-shadow: -2.286rem -0.286rem 0 #ddd, -1.143rem 0 0 #ddd,
      1.143rem 0 0 #ddd, 2.286rem 0 0 #ddd;
    -webkit-transform: translateY(0);
    transform: translateY(0);
  }
  10% {
    box-shadow: -2.286rem -0.429rem 0 #999, -1.143rem -0.286rem 0 #ddd,
      1.143rem 0 0 #ddd, 2.286rem 0 0 #ddd;
    -webkit-transform: translateY(0);
    transform: translateY(0);
  }
  15% {
    background-color: #ddd;
    box-shadow: -2.286rem 0.143rem 0 #ddd, -1.143rem -0.143rem 0 #999,
      1.143rem 0.286rem 0 #ddd, 2.286rem 0.286rem 0 #ddd;
    -webkit-transform: translateY(-0.286rem);
    transform: translateY(-0.286rem);
  }
  20% {
    background-color: #999;
    box-shadow: -2.286rem 0.429rem 0 #ddd, -1.143rem 0.286rem 0 #ddd,
      1.143rem 0.143rem 0 #ddd, 2.286rem 0.429rem 0 #ddd;
    -webkit-transform: translateY(-0.429rem);
    transform: translateY(-0.429rem);
  }
  25% {
    background-color: #ddd;
    box-shadow: -2.286rem 0.143rem 0 #ddd, -1.143rem 0.143rem 0 #ddd,
      1.143rem -0.286rem 0 #999, 2.286rem -0.143rem 0 #ddd;
    -webkit-transform: translateY(-0.143rem);
    transform: translateY(-0.143rem);
  }
  30% {
    box-shadow: -2.286rem 0 0 #ddd, -1.143rem 0 0 #ddd,
      1.143rem -0.143rem 0 #ddd, 2.286rem -0.429rem 0 #999;
    -webkit-transform: translateY(0);
    transform: translateY(0);
  }
  35% {
    box-shadow: -2.286rem 0 0 #ddd, -1.143rem 0 0 #ddd, 1.143rem 0 0 #ddd,
      2.286rem -0.143rem 0 #ddd;
  }
  40% {
    box-shadow: -2.286rem 0 0 #ddd, -1.143rem 0 0 #ddd, 1.143rem 0 0 #ddd,
      2.286rem 0 0 #ddd;
  }
  to {
    box-shadow: -2.286rem 0 0 #ddd, -1.143rem 0 0 #ddd, 1.143rem 0 0 #ddd,
      2.286rem 0 0 #ddd;
  }
}
</style>
