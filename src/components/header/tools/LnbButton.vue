<template>
  <li :class="{ notice: notice }">
    <button
      class="header-lnb__button"
      :class="{ active: view === keyvalue }"
      @click="$listeners['click']"
    >
      <img class="header-lnb__image" :src="image" />
      <span class="header-lnb__text">{{ text }}</span>
    </button>
  </li>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'LnbButton',
  props: {
    text: String,
    keyvalue: String,
    image: String,
    notice: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    ...mapGetters(['view']),
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
