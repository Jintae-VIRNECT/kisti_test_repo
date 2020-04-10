<template>
  <section class="tab-view">
    <div class="tab-view__header">
      <span class="tab-view__title">{{ title }}</span>
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
        :title="'최근 통화 목록이 없습니다.'"
        :description="' 원격 협업을 시작해보세요.'"
      ></show-empty>
    </div>
  </section>
</template>

<script>
import ShowEmpty from 'ShowEmpty'
import Search from 'Search'
export default {
  name: 'WorkspaceTabView',
  components: {
    Search,
    ShowEmpty,
  },
  props: {
    empty: {
      type: Boolean,
      default: true,
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
