<template>
  <section class="roominfo-view">
    <p class="roominfo-view__title">
      참가자 정보
    </p>
    <div class="roominfo-view__body">
      <scroller>
        <wide-card
          v-for="user of users"
          :key="user.userId"
          :customClass="[
            'remoteinfo-usercard',
            { offline: user.status === 'offline' },
          ]"
        >
          <div class="roominfo-userinfo">
            <profile
              :image="user.path"
              :mainText="user.userName"
              :subText="user.userEmail"
              :role="user.userRole"
            ></profile>

            <img
              v-if="user.deviceType === 'web'"
              class="userinfo__image"
              :src="require('assets/image/ic_monitor.svg')"
            />
            <img
              v-else-if="user.deviceType === 'smartphone'"
              class="userinfo__image"
              :src="require('assets/image/ic_mobile.svg')"
            />
            <img
              v-else
              class="userinfo__image"
              :src="require('assets/image/ic_hololens.svg')"
            />
            <button
              class="btn line userinfo__button"
              @click="removeUser(user.userId)"
              v-if="leader"
            >
              내보내기
            </button>
          </div>
        </wide-card>
      </scroller>
    </div>
  </section>
</template>

<script>
import { leaveRoom, participantsList } from 'api/remote/room'
import WideCard from 'WideCard'
import Scroller from 'Scroller'
import Profile from 'Profile'
export default {
  name: 'ModalParticipantsInfo',
  components: {
    WideCard,
    Profile,
    Scroller,
  },
  props: {
    participants: {
      type: Array,
      default: () => {
        return []
      },
    },
    leader: {
      type: Boolean,
      default: false,
    },
    roomId: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      name: '',
      description: '',
      users: [],
    }
  },
  watch: {
    participants: {
      deep: true,
      handler() {
        this.user = this.participants
      },
    },
  },
  methods: {
    async removeUser(id) {
      try {
        const removeRtn = await leaveRoom({
          roomId: this.roomId,
          participantsId: id,
        })
        if (removeRtn) {
          const participants = await participantsList({
            roomId: this.roomId,
          })
          this.users = participants.participants
        }
      } catch (err) {
        console.error(err)
      }
    },
  },

  /* Lifecycles */
  mounted() {
    if (this.participants.length > 0) {
      this.users = this.participants
    }
  },
}
</script>
