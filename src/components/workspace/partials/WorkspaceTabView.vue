<template>
  <section class="tab-view" :class="[customClass]">
    <div class="tab-view__header offsetwidth">
      <span
        class="tab-view__title"
        :class="{
          'workspace-selected': workspace && workspace.uuid,
        }"
        >{{ title }}</span
      >
      <span class="tab-view__count" v-if="!(listCount === null)">
        {{ listCount }}
      </span>
      <div class="tab-view__tools">
        <search
          v-if="placeholder.length > 0"
          :placeholder="placeholder"
          appendClass="tab-view__search"
          @search="text => $emit('search', text)"
        ></search>
        <icon-button
          class="delete"
          v-if="showDeleteButton"
          :imgSrc="require('assets/image/ic_delete.svg')"
          :text="deleteButtonText ? deleteButtonText : $t('button.remove')"
          @click="$emit('delete')"
        ></icon-button>
        <icon-button
          class="refresh"
          v-if="showRefreshButton"
          :imgSrc="require('assets/image/workspace/ic_renew.svg')"
          animation="rotate360"
          :text="$t('button.refresh')"
          @click="$emit('refresh')"
        ></icon-button>
      </div>
    </div>
    <!-- <div class="tab-view__sub-header offsetwidth" v-if="!hideTitle">
      <span class="tab-view__description">{{ description }}</span>
      <div class="tab-view__tools" v-if="description.length > 0">
        <search
          v-if="placeholder.length > 0"
          :placeholder="placeholder"
          appendClass="tab-view__search"
          @search="text => $emit('search', text)"
        ></search>
        <icon-button
          v-if="showDeleteButton"
          :imgSrc="require('assets/image/ic_delete.svg')"
          :text="deleteButtonText ? deleteButtonText : $t('button.remove')"
          @click="$emit('delete')"
        ></icon-button>
        <icon-button
          v-if="showRefreshButton"
          :imgSrc="require('assets/image/workspace/ic_renew.svg')"
          animation="rotate360"
          :text="$t('button.refresh')"
          @click="$emit('refresh')"
        ></icon-button>
      </div>
    </div> -->
    <div class="tab-view__body offsetwidth">
      <slot v-if="!empty && !loading"></slot>
      <show-empty
        v-else
        :image="emptyImage"
        :title="emptyTitle"
        :description="emptyDescription"
      ></show-empty>
      <transition name="loading">
        <div class="tab-view__loading loading" v-if="loading">
          <div class="tab-view__loading-inner">
            <div class="tab-view__loading-logo">
              <img src="~assets/image/gif_loading.svg" />
            </div>
          </div>
        </div>
      </transition>
    </div>
  </section>
</template>

<script>
import ShowEmpty from 'ShowEmpty'
import Search from 'Search'
import IconButton from 'IconButton'
export default {
  name: 'WorkspaceTabView',
  components: {
    Search,
    ShowEmpty,
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
      default: null,
    },
    hideTitle: {
      type: Boolean,
      default: false,
    },
    loading: {
      type: Boolean,
      default: false,
    },
  },
}
</script>
