<template>
  <section class="tab-view" :class="[customClass]">
    <div class="tab-view__header offsetwidth">
      <span class="tab-view__title">{{ title }}</span>
      <list-badge
        v-if="!(listCount === null)"
        :listCount="listCount"
      ></list-badge>
    </div>
    <div class="tab-view__sub-header offsetwidth">
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
          :text="deleteButtonText"
          @click="$emit('delete')"
        ></icon-button>
        <icon-button
          v-if="showRefreshButton"
          :imgSrc="require('assets/image/back/mdpi_icn_renew.svg')"
          :text="'새로고침'"
          @click="$emit('refresh')"
        ></icon-button>
      </div>
    </div>
    <div class="tab-view__body offsetwidth">
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
    customClass: {
      type: String,
      default: '',
    },
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
    listCount: {
      type: [Number, String],
      default: null,
    },
    showRefreshButton: {
      type: Boolean,
      default: false,
    },
    showDeleteButton: {
      type: Boolean,
      default: false,
    },
    deleteButtonText: {
      type: String,
      default: '삭제하기',
    },
  },
  data() {
    return {}
  },
  computed: {
    deleteListeners: function() {
      const _this = this
      return Object.assign({}, this.$listeners, {
        click: function(event) {
          _this.$eventBus.$emit('dataList:delete')
        },
      })
    },
    refreshListeners: function() {
      const _this = this
      return Object.assign({}, this.$listeners, {
        click: function(event) {
          _this.$eventBus.$emit('dataList:refresh')
        },
      })
    },
  },
  watch: {},
  methods: {
    refresh() {},
  },

  /* Lifecycles */
  mounted() {},
}
</script>
