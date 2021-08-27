<template>
  <section class="createroom-user">
    <div class="createroom-user__header">
      <p class="createroom-user__title">
        {{ $t('workspace.create_select_member_list') }}
        <span class="createroom-user__number">{{ totalNum }}</span>
      </p>
      <r-select-check
        v-if="showMemberGroupSelect"
        class="createroom-user--group-selects"
        :options="groupList"
        value="groupId"
        text="groupName"
        subText="memberCount"
        :selectedValue.sync="selectedGroupId"
      ></r-select-check>
      <icon-button
        class="refresh"
        :text="$t('button.refresh')"
        :imgSrc="require('assets/image/workspace/ic_renew.svg')"
        animation="rotate360"
        @click="refresh"
      ></icon-button>
    </div>
    <div class="createroom-user__body">
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
            :height="height"
            @click.native="selectUser(user)"
          >
            <profile
              class="profile-short"
              :image="user.profile"
              :mainText="user.nickname || user.nickName"
              :subText="user.email"
              :role="user.role"
              :thumbStyle="thumbStyle"
              :status="accessType(user.accessType)"
            ></profile>
          </wide-card>
        </div>
      </scroller>
      <div v-else class="createroom-user__empty">
        <div class="createroom-user__empty-box">
          <img src="~assets/image/img_inviteuser_empty.svg" />
          <p class="createroom-user__empty-title">
            {{ $t('workspace.create_select_empty_title') }}
          </p>
          <p class="createroom-user__empty-description">
            {{ $t('workspace.create_select_empty_description') }}
          </p>
        </div>
      </div>
      <transition name="loading">
        <div class="createroom-user__loading loading" v-if="loading">
          <div class="createroom-user__loading-logo">
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
import responsiveCardMixin from 'mixins/responsiveCard'
import RSelectCheck from '../modules/RemoteSelectCheck'

export default {
  name: 'ModalCreateRoomInvite',
  components: {
    Scroller,
    Profile,
    WideCard,
    IconButton,
    RSelectCheck,
  },
  mixins: [responsiveCardMixin],
  data() {
    return {
      selectedGroupId: '',
      height: defaultWideCardHeight,
      thumbStyle: defaultThumbStyle,
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
  src="assets/style/workspace/workspace-createroom-invite.scss"
></style>
<style lang="scss">
@import '~assets/style/mixin';
@include responsive-mobile {
  .createroom-user__header .icon-button > img {
    content: url(~assets/image/workspace/ic_renew_16.svg);
  }

  .createroom-user__body {
    .widecard.choice {
      @include responsive-widecard;
    }
    .createroom-user__empty {
      background-color: $new_color_bg_sub;
      img {
        width: 20rem;
        content: url('~assets/image/img_inviteuser_empty_new.svg');
      }
      .createroom-user__empty-title {
        margin-top: 1.6rem;
        @include fontLevel(200);
        color: $new_color_text_main;
      }
      .createroom-user__empty-description {
        padding-top: 0.2rem;
        color: $new_color_text_sub_description;
      }
    }
    .createroom-user__loading.loading {
      background-color: $new_color_bg_sub;
    }
  }
}

.createroom-user--group-selects {
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
