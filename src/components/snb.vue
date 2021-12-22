<template>
  <nav class="snb-wrapper" ref="snb" :class="{ sticky: snbSticky }">
    <ul class="snb">
      <li
        v-for="(array, product) in products"
        :id="`tab-${product}`"
        :key="`${product}-tab`"
        :class="{ active: product === activeTab }"
      >
        <button @click="tabClick(product)">
          {{ $t(`home.${product}`) }}
        </button>
      </li>
    </ul>
  </nav>
</template>

<script>
export default {
  props: ['products', 'activeTab'],
  data() {
    return {
      snbSticky: false,
      snbTop: 0,
    }
  },
  methods: {
    tabClick(product) {
      this.$emit('tabClick', product)
    },
    snbNav() {
      let scrollY = window.pageYOffset
      let elHeight = this.$refs.snb.offsetHeight
      this.snbTop = document.querySelector('#subVisualSection').offsetHeight
      if (scrollY - elHeight - 52 > this.snbTop) this.snbSticky = true
      else this.snbSticky = false
    },
  },
  mounted() {
    window.addEventListener('scroll', this.snbNav)
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.snbNav)
  },
}
</script>
