<template>
  <section class="roominfo-view">
    <p class="roominfo-view__title">
      {{ $t('workspace.info_remote_member') }}
    </p>
    <div class="roominfo-view__body">
      <scroller>
        <user-info
          v-for="user of participants"
          :key="user.uuid"
          :user="user"
          :isLeader="isLeader"
          @kickout="kickout(user.uuid)"
        ></user-info>
      </scroller>
    </div>
  </section>
</template>

<script>
import { kickoutMember } from 'api/workspace'
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
    async kickout(id) {
      if (this.checkBeta()) return
      if (this.account.uuid === id) return
      try {
        const removeRtn = await kickoutMember({
          sessionId: this.sessionId,
          workspaceId: this.workspace.uuid,
          leaderId: this.account.uuid,
          participantId: id,
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
