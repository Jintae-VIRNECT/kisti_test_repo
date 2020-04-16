<template>
  <section class="tab-view">
    <div class="tab-view__header">
      <span class="tab-view__title">{{ title }}</span>
      <ListBadge v-if="showListCountBadge"></ListBadge>
    </div>
    <div class="tab-view__sub-header">
      <span class="tab-view__description" v-if="description.length > 0">{{
        description
      }}</span>
      <div class="tab-view__tools">
        <search
          v-if="placeholder.length > 0"
          :placeholder="placeholder"
          appendClass="tab-view__search"
        ></search>
        <icon-button
          v-if="showDeleteButton"
          :imgSrc="require('assets/image/back/mdpi_tr.svg')"
          :text="'삭제하기'"
        ></icon-button>
        <icon-button
          v-if="showRefreshButton"
          :imgSrc="require('assets/image/back/mdpi_icn_renew.svg')"
          :text="'새로고침'"
        ></icon-button>
      </div>
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
import IconButton from 'IconButton'
export default {
  name: 'WorkspaceTabView',
  components: {
    Search,
    ShowEmpty,
    ListBadge,
    IconButton,
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
    showListCountBadge: {
      type: Boolean,
      default: false,
    },
    showRefreshButton: {
      type: Boolean,
      default: false,
    },
    showDeleteButton: {
      type: Boolean,
      default: false,
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
