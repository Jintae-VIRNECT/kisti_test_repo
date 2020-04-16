<template>
  <section class="tab-view">
    <div class="tab-view__header">
      <span class="tab-view__title">{{ title }}</span>
      <ListBadge></ListBadge>
    </div>
    <div class="tab-view__sub-header">
      <span class="tab-view__description" v-if="description.length > 0">{{
        description
      }}</span>
      <search
        v-if="placeholder.length > 0"
        :placeholder="placeholder"
        appendClass="tab-view__search"
      ></search>
    </div>
    <div class="tab-view__body">
      <slot v-if="!empty"></slot>
      <show-empty
        v-else
        :image="emptyImage"
        :title="emptyTitle"
        :description="emptyDescription"
      ></show-empty>
    </div>
  </section>
</template>

<script>
import ShowEmpty from 'ShowEmpty'
import Search from 'Search'
import ListBadge from 'ListBadge'
export default {
  name: 'WorkspaceTabView',
  components: {
    Search,
    ShowEmpty,
    ListBadge,
  },
  props: {
    empty: {
      type: Boolean,
      default: false,
    },
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
    emptyTitle: {
      type: String,
      default: '',
    },
    emptyDescription: {
      type: String,
      default: '',
    },
    emptyImage: {
      type: String,
      default: require('assets/image/img_remote_empty.svg'),
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
