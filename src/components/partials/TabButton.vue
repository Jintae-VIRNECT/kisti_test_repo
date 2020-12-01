<template>
  <li class="dashboard-tab__button">
    <button
      :class="{ active: active, nolicense: !(hasLicense && !expireLicense) }"
    >
      <img class="dashboard-tab__button--icon" :src="imgSrc" alt="" />
      {{ text }}
    </button>
  </li>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  name: 'TabButton',
  props: {
    text: {
      type: String,
      required: true,
    },
    active: {
      type: Boolean,
      default: false,
    },
    images: {
      type: Object,
    },
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['expireLicense']),
    imgSrc() {
      return this.active ? this.images.on : this.images.off
    },
  },
}
</script>
