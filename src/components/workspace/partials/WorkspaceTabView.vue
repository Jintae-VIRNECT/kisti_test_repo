<template>
  <section class="tab-view" :class="[customClass]">
    <div class="tab-view__header offsetwidth" :class="{ sub: showSubHeader }">
      <span class="tab-view__title">{{ title }}</span>
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

        <!-- 즐겨 찾기 -->
        <icon-button
          v-if="showManageGroupButton"
          :imgSrc="require('assets/image/workspace/ic_manage_member_group.svg')"
          :text="$t('button.member_group_bookmark')"
          @click="$emit('showgroup')"
        ></icon-button>

        <!-- 멤버 화면으로 가는 버튼 -->
        <icon-button
          v-if="showMemberButton"
          :imgSrc="require('assets/image/workspace/ic_member.svg')"
          :text="$t('button.member')"
          @click="$emit('showmember')"
        ></icon-button>

        <!-- 그룹 추가 버튼 -->
        <icon-button
          v-if="showAddGroupButton"
          :imgSrc="require('assets/image/workspace/ic_add_member_group_.svg')"
          :text="$t('button.add_group_bookmark')"
          @click="$emit('addgroup')"
        ></icon-button>
      </div>
    </div>
    <div class="tab-view__sub-header offsetwidth" v-if="showSubHeader">
      <span class="tab-view__description" v-if="description.length > 0">{{
        description
      }}</span>
    </div>
    <div class="tab-view__body offsetwidth">
      <slot name="modal"></slot>
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
    showManageGroupButton: {
      type: Boolean,
      default: false,
    },
    showMemberButton: {
      type: Boolean,
      default: false,
    },
    showAddGroupButton: {
      type: Boolean,
      default: false,
    },
    deleteButtonText: {
      type: String,
      default: null,
    },

    showSubHeader: {
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
