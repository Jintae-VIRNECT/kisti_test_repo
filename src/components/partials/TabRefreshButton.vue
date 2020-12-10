<template>
  <button
    @click="refresh"
    class="dashboard-tab--refresh"
    @mouseenter="toggleHover"
    @mouseleave="toggleHover"
  >
    <img :src="imgSrc" alt="refIcon" />
    <p>{{ $t('button.refresh') }}</p>
  </button>
</template>

<script>
export default {
  name: 'TabRefreshButton',
  data() {
    return {
      hover: false,
    }
  },
  computed: {
    imgSrc() {
      return this.hover
        ? require('assets/image/ic_refresh_hover.svg')
        : require('assets/image/ic_refresh.svg')
    },
  },
  methods: {
    refresh() {
      this.$eventBus.$emit('refresh:chart')
    },
    toggleHover() {
      this.hover = !this.hover
    },
  },
}
</script>

<style></style>
