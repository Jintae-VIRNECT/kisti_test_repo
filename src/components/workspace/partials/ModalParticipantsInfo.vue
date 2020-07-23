<template>
  <section class="roominfo-view">
    <p class="roominfo-view__title">
      참가자 정보
    </p>
    <div class="roominfo-view__body">
      <scroller>
        <user-info
          v-for="user of participants"
          :key="user.uuid"
          :user="user"
          :isLeader="isLeader"
          @removeUser="removeUser(user.uuid)"
        ></user-info>
      </scroller>
    </div>
  </section>
</template>

<script>
import { leaveRoom } from 'api/workspace/room'
import Scroller from 'Scroller'
import UserInfo from 'UserInfo'
export default {
  name: 'ModalParticipantsInfo',
  components: {
    Scroller,
    UserInfo,
  },
  props: {
    participants: {
      type: Array,
      default: () => {
        return []
      },
    },
    isLeader: {
      type: Boolean,
      default: false,
    },
    sessionId: {
      type: String,
      required: true,
    },
  },
  data() {
    return {
      name: '',
      description: '',
    }
  },
  methods: {
    async removeUser(id) {
      try {
        const removeRtn = await leaveRoom({
          sessionId: this.sessionId,
          participantsId: id,
        })
        if (removeRtn) {
          // participants 제거
        }
      } catch (err) {
        console.error(err)
      }
    },
  },
}
</script>
