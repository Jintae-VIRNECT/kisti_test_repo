<template>
  <section class="createroom-user">
    <div class="createroom-user__header">
      <p class="createroom-user__title">
        {{ $t('workspace.create_select_member_list') }}
        <span class="createroom-user__number">{{ totalNum }}</span>
      </p>
      <icon-button
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
            height="6.143em"
            @click.native="selectUser(user)"
          >
            <profile
              class="profile-short"
              :image="user.profile"
              :mainText="user.nickname || user.nickName"
              :subText="user.email"
              :role="user.role"
              :thumbStyle="{ width: '3em', height: '3em' }"
              :status="accessType(user.accessType)"
            ></profile
          ></wide-card>
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

export default {
  name: 'ModalCreateRoomInvite',
  components: {
    Scroller,
    Profile,
    WideCard,
    IconButton,
  },
  data() {
    return {}
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
  methods: {
    refresh() {
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

  /* Lifecycles */
  mounted() {},
}
</script>

<style
  lang="scss"
  scoped
  src="assets/style/workspace/workspace-createroom-invite.scss"
></style>
