<template>
  <ul class="depth-nav" :data-heading="gnb.text">
    <li
      class="depth-item"
      v-for="(depth, idx) of gnb.array"
      :key="idx"
      :class="{ active: $route.name === depth.page }"
    >
      <button
        @click="chnRoute(depth.to)"
        v-html="depth.text"
        v-if="depth.page !== 'blank'"
      ></button>
      <a :href="depth.to" target="_blank" v-else>{{ depth.text }}</a>
      <ul v-if="depth.depth" class="depth-inner-nav">
        <li
          v-for="(inner, i) of depth.depth"
          :key="i"
          :class="{ active: $route.path === inner.to }"
        >
          <button @click="chnRoute(inner.to)" v-html="inner.text"></button>
        </li>
      </ul>
    </li>
  </ul>
</template>

<script>
export default {
  props: {
    gnb: {
      type: Object,
    },
  },
  methods: {
    chnRoute(route) {
      this.$emit('chnRoute', route, false)
    },
  },
}
</script>
