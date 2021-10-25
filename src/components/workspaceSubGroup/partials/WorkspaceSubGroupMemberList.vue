<template>
  <section class="sub-group-member">
    <div class="sub-group-member__header">
      <p class="sub-group-member__title">
        {{ $t('workspace.create_select_member_list') }}
        <span class="sub-group-member__number">{{ totalNum }}</span>
      </p>
      <icon-button
        class="refresh"
        :text="$t('button.refresh')"
        :imgSrc="require('assets/image/ic_refresh.svg')"
        :activeImgSrc="require('assets/image/ic_refresh.svg')"
        :selectImgSrc="require('assets/image/ic_refresh.svg')"
        @click="refresh"
      ></icon-button>
    </div>
    <div class="sub-group-member__body">
      <scroller v-if="users.length > 0">
        <div>
          <wide-card
            v-for="(user, idx) of users"
            :key="idx"
            :customClass="{
              choice: true,
              selected:
                selection.findIndex(select => select.uuid === user.uuid) > -1,
            }"
            :height="'6.143em'"
            @click.native="selectUser(user)"
          >
            <profile
              class="profile-short"
              :image="user.profile"
              :mainText="user.nickname || user.nickName"
              :subText="user.email"
              :role="user.role"
              :thumbStyle="{ width: '3em', height: '3em' }"
              :status="accessType()"
            ></profile>
          </wide-card>
        </div>
      </scroller>
      <div v-else class="sub-group-member__empty">
        <div class="sub-group-member__empty-box">
          <img src="~assets/image/img_inviteuser_empty.svg" />
          <p class="sub-group-member__empty-title">
            {{ $t('workspace.create_select_empty_title') }}
          </p>
          <p class="sub-group-member__empty-description">
            {{ $t('workspace.create_select_empty_description') }}
          </p>
        </div>
      </div>
      <transition name="loading">
        <div class="sub-group-member__loading loading" v-if="loading">
          <div class="sub-group-member__loading-logo">
            <img src="~assets/image/gif_loading.svg" />
          </div>
        </div>
      </transition>
    </div>
  </section>
</template>

<script>
import Scroller from 'Scroller'
import Profile from 'Profile'
import WideCard from 'WideCard'
import IconButton from 'IconButton'

export default {
  name: 'WorkspaceSubGroupMemberList',
  components: {
    Scroller,
    Profile,
    WideCard,
    IconButton,
  },

  data() {
    return {
      selectedGroupId: '',
      responsiveFn: null,
    }
  },
  props: {
    users: {
      type: Array,
      default: () => {
        return []
      },
    },
    selection: {
      type: Array,
      default: () => {
        return []
      },
    },
    total: {
      type: [Number, Boolean],
      default: false,
    },
    loading: {
      type: Boolean,
      default: false,
    },
    showMemberGroupSelect: {
      type: Boolean,
      default: false,
    },
    groupList: {
      type: Array,
      default: () => [],
    },
  },
  computed: {
    totalNum() {
      if (this.total === false) {
        return this.users.length
      } else {
        return this.total
      }
    },
  },
  watch: {
    selectedGroupId() {
      this.$emit('selectedgroupid', this.selectedGroupId)
    },
  },
  methods: {
    refresh() {
      this.selectedGroupId = 'NONE'
      this.$emit('inviteRefresh')
    },
    selectUser(user) {
      this.$emit('userSelect', user)
    },
    accessType(accessType) {
      if (accessType) return accessType.toLowerCase()
      return ''
    },
  },
}
</script>

<style
  lang="scss"
  scoped
  src="assets/style/dashboard/dashboard-workspace-sub-group-member-list.scss"
></style>
<style lang="scss">
@import '~assets/style/mixin';

.sub-group-member--group-selects {
  .select-label {
    position: absolute;
    top: -0.571em;
    right: 7.4286rem;
    width: 12.4286rem;
    min-width: 12.4286rem;
    height: 2.4286rem;
    min-height: 2.4286rem;
    padding: 0.5002rem 2.2862rem 0.6429rem 0.8576rem;
    background-color: #1a1a1b;
    border-radius: 2px;
    &::after {
      right: 0.994px;
    }
    &:hover {
      background-color: #1a1a1b;
    }
    &:active {
      background-color: #1a1a1b;
    }
    &:focus {
      background-color: #1a1a1b;
    }
  }
}
</style>
