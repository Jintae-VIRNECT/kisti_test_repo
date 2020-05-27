<template>
  <main class="main-wrapper ar">
    <div class="stream-body">
      <ar-tools></ar-tools>
      <menus></menus>
    </div>
  </main>
</template>

<script>
import ArTools from './tools/ArTools'
import Menus from './tools/Menus'
export default {
  name: 'ServiceAr',
  components: {
    ArTools,
    Menus,
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
