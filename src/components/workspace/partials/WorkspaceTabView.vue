<template>
  <section class="tab-view">
    <div class="tab-view__header">
      <span class="tab-view__title">{{ title }}</span>
      <span class="tab-view__description" v-if="description.length > 0">{{
        description
      }}</span>
      <search v-if="placeholder.length > 0" :placeholder="placeholder"></search>
    </div>
    <div class="tab-view__body">
      <slot></slot>
    </div>
  </section>
</template>

<script>
import Search from 'Search'
export default {
  name: 'WorkspaceTabView',
  components: {
    Search,
  },
  props: {
    title: {
      type: String,
      required: true,
    },
    description: {
      type: String,
      default: '',
    },
    placeholder: {
      type: String,
      default: '',
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
