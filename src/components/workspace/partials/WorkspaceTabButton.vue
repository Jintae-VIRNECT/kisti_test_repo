<template>
  <li class="workspace-tab__button">
    <button :class="{ active: active, nolicense: !license }">
      {{ text }}
    </button>
  </li>
</template>

<script>
export default {
  name: 'WorkspaceTabButton',
  props: {
    text: {
      type: String,
      required: true,
    },
    active: {
      type: Boolean,
      default: false,
    },
    license: {
      type: Boolean,
      default: true,
    },
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
