<template>
  <section class="createroom-user">
    <div class="createroom-user__header">
      <p class="createroom-user__title">
        선택 가능한 멤버 리스트
        <span class="createroom-user__number">{{ users.length }}</span>
      </p>
      <icon-button
        text="새로고침"
        :imgSrc="require('assets/image/back/mdpi_icn_renew.svg')"
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
                selection.findIndex(select => select.userId === user.userId) >
                -1,
            }"
            @click.native="selectUser(user)"
          >
            <profile
              :image="user.path"
              :mainText="user.userName"
              :subText="user.userEmail"
              :status="user.status"
              :role="user.userRole"
            ></profile
          ></wide-card>
        </div>
      </scroller>
      <div v-else class="createroom-user__empty">
        <p class="createroom-user__empty-title">
          협업 가능한 멤버가 없습니다.
        </p>
        <p class="createroom-user__empty-description">
          협업 멤버를 추가해주세요.
        </p>
      </div>
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
  },
  watch: {},
  methods: {
    reset() {
      this.selection = []
    },
    refresh() {
      this.$emit('inviteRefresh')
    },
    selectUser(user) {
      this.$emit('userSelect', user)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style
  lang="scss"
  scoped
  src="assets/style/workspace/workspace-createroom.scss"
></style>
