<template>
  <section class="roominfo-view">
    <p class="roominfo-view__title">
      {{ $t('workspace.info_remote_member') }}
    </p>
    <div class="roominfo-view__body table">
      <scroller>
        <user-info
          v-for="user of participants"
          :key="user.uuid"
          :user="user"
          :isLeader="isLeader"
          @kickout="kickout(user)"
        ></user-info>
      </scroller>
    </div>
  </section>
</template>

<script>
import Scroller from 'Scroller'
import UserInfo from 'UserInfo'
import confirmMixin from 'mixins/confirm'
export default {
  name: 'ModalParticipantsInfo',
  mixins: [confirmMixin],
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
    kickout(user) {
      this.confirmCancel(
        this.$t('service.participant_kick_confirm', {
          name: user.nickName,
        }),
        {
          text: this.$t('button.confirm'),
          action: () => {
            this.$emit('kickout', user.uuid)
          },
        },
        {
          text: this.$t('button.cancel'),
        },
      )
    },
  },
}
</script>
