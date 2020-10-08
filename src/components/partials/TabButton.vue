<template>
  <li class="dashboard-tab__button">
    <button
      :class="{ active: active, nolicense: !(hasLicense && !expireLicense) }"
    >
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
  },
  data() {
    return {}
  },
  computed: {
    ...mapGetters(['expireLicense']),
  },
  watch: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>
