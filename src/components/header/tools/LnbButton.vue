<template>
  <li :class="{ notice: notice }">
    <button
      class="header-lnb__button"
      :class="{ active: active }"
      @click="$listeners['click']"
    >
      <img class="header-lnb__image" :src="image" />
      <span class="header-lnb__text">{{ text }}</span>
    </button>
  </li>
</template>

<script>
export default {
  name: 'LnbButton',
  props: {
    text: String,
    active: Boolean,
    image: String,
    notice: {
      type: Boolean,
      default: false,
    },
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
