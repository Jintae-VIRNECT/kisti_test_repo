<template>
  <button
    class="header-snb__button"
    :class="{ active: view === keyvalue }"
    @click="changeCallMode(keyvalue)"
  >
    <img class="header-snb__image" :src="image" />
    <span class="header-snb__text">{{ text }}</span>
  </button>
</template>

<script>
import { mapGetters, mapActions } from 'vuex'
export default {
  name: 'SnbButton',
  props: {
    text: String,
    keyvalue: String,
    image: String,
  },
  computed: {
    ...mapGetters(['view']),
  },
  watch: {},
  methods: {
    ...mapActions(['changeCallMode']),
  },

  /* Lifecycles */
  mounted() {},
}
</script>
