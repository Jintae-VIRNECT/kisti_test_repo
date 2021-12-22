<template>
  <section class="createroom-user">
    <div class="createroom-user__header">
      <p class="createroom-user__title">
        {{ $t('workspace.create_select_member_list') }}
        <span class="createroom-user__number">{{ totalNum }}</span>
      </p>
      <r-select-check
        v-if="showMemberGroupSelect && !isMobileSize"
        class="createroom-user--group-selects"
        :options="groupList"
        value="groupId"
        text="groupName"
        subText="memberCount"
        :selectedValue.sync="selectedGroupId"
      ></r-select-check>

      <div v-if="isMobileSize" class="header__tools">
        <popover
          v-if="isMobileSize"
          trigger="click"
          popperClass="member-group-menu"
          placement="bottom-end"
          width="auto"
          scrollHide
          :useTopMargin="10"
        >
          <icon-button
            slot="reference"
            v-if="showMemberGroupSelect && isMobileSize"
            class="favourite"
            :imgSrc="
              require('assets/image/workspace/ic_manage_member_group.svg')
            "
            :text="$t('button.member_group_bookmark')"
            @click="() => {}"
          ></icon-button>

          <ul class="groupcard-popover">
            <li v-for="group of groupList" :key="group.groupId">
              <button
                class="group-pop__button"
                @click="updateSelectedGroupId(group.groupId)"
              >
                {{ group.groupName }}
              </button>
            </li>
          </ul>
        </popover>

        <icon-button
          class="refresh"
          :text="$t('button.refresh')"
          :imgSrc="require('assets/image/workspace/ic_renew.svg')"
          animation="rotate360"
          @click="refresh"
        ></icon-button>
      </div>
      <icon-button
        v-else
        class="refresh"
        :text="$t('button.refresh')"
        :imgSrc="require('assets/image/workspace/ic_renew.svg')"
        animation="rotate360"
        @click="refresh"
      ></icon-button>
    </div>
    <div class="createroom-user__body">
      <scroller v-if="totalNum > 0">
        <div>
          <collapsible
            class="member-collapsible"
            :decorated="!isMobileSize"
            v-for="subGroup in filteredSubGroups"
            :title="subGroup.groupName"
            :key="'group_' + subGroup.groupId"
            :count="subGroup.remoteGroupMemberResponses.length"
            :preOpen="preOpen"
          >
            <wide-card
              v-for="(user, idx) of subGroup.remoteGroupMemberResponses"
              :key="idx"
              :customClass="{
                choice: true,
                selected:
                  selection.findIndex(select => select.uuid === user.uuid) > -1,
              }"
              :height="'100%'"
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
          </collapsible>
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
import Collapsible from 'Collapsible'
import Popover from 'Popover'
import responsiveCardMixin from 'mixins/responsiveCard'
import RSelectCheck from '../modules/RemoteSelectCheck'
import { ROLE } from 'configs/remote.config'

export default {
  name: 'ModalCreateRoomInvite',
  components: {
    Scroller,
    Profile,
    WideCard,
    Collapsible,
    IconButton,
    RSelectCheck,
    Popover,
  },
  mixins: [responsiveCardMixin],
  data() {
    return {
      selectedGroupId: '',
    }
  },
  props: {
    subGroups: {
      type: Array,
      default: () => [],
    },
    selection: {
      type: Array,
      default: () => {
        return []
      },
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
      return this.filteredSubGroups.reduce((acc, subGroup) => {
        return acc + subGroup.remoteGroupMemberResponses.length
      }, 0)
    },
    filteredSubGroups() {
      return this.subGroups.map(subGroup => {
        subGroup.remoteGroupMemberResponses = subGroup.remoteGroupMemberResponses.filter(
          member => member.role !== ROLE.GUEST,
        )
        return subGroup
      })
    },
    preOpen() {
      if (
        this.subGroups.length === 1 &&
        this.subGroups[0].groupId === 'group_etc'
      ) {
        return true
      } else {
        return false
      }
    },
  },
  watch: {
    selectedGroupId() {
      this.$eventBus.$emit('update::selectedgroupid', this.selectedGroupId)
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
    updateSelectedGroupId(groupId) {
      this.$eventBus.$emit('popover:close')
      this.selectedGroupId = groupId
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
  .createroom-user__header .icon-button.refresh > img {
    content: url(~assets/image/workspace/ic_renew_16.svg);
  }

  .createroom-user__header .icon-button.favourite > img {
    content: url(~assets/image/workspace/ic_manage_member_group_16.svg);
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

.member-collapsible {
  &.collapsible.decorated {
    margin-bottom: 0.5714rem;
  }

  .widecard {
    &.choice {
      border-radius: 4px;
      padding: 0.8571rem 1.1429rem;
    }

    &.choice:first-of-type {
      margin-top: 0.7857rem;
    }

    &.choice:last-of-type {
      margin-bottom: 0;
    }
  }
}
@include responsive-mobile {
  .member-collapsible {
    .widecard {
      &.choice {
        border-radius: 4px;
        padding: 0.8571rem 1.1429rem;
        margin-bottom: 0.7143rem;
      }

      &.choice:first-of-type {
        margin-top: 0;
      }

      &.choice:last-of-type {
        margin-bottom: 1.0714rem;
      }
    }
  }
  .header__tools {
    display: flex;
  }

  .member-group-menu {
    z-index: 110;
    min-width: 16.2857rem;
    > .popover--body {
      padding: 0;
    }

    .groupcard-popover {
      width: 227.9998px;

      .group-pop__button {
        width: 100%;
      }
    }

    @include responsive-popover;
  }
}
</style>
