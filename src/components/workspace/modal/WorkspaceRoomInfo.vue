<template>
  <modal
    :title="$t('workspace.info_remote_detail')"
    width="64.286em"
    height="56.429em"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    customClass="modal-roominfo"
  >
    <div class="roominfo">
      <section class="roominfo-nav">
        <div class="roominfo-nav__image">
          <profile
            :group="true"
            :image="image"
            :thumbStyle="{ width: '5.143rem', height: '5.143rem' }"
          ></profile>
        </div>
        <button
          class="roominfo-nav__menu"
          :class="{ active: tabview === 'group' }"
          @click="tabChange('group')"
        >
          {{ $t('workspace.info_remote') }}
        </button>
        <button
          class="roominfo-nav__menu"
          :class="{ active: tabview === 'user' }"
          @click="tabChange('user')"
        >
          {{ $t('workspace.info_remote_member') }}
        </button>
      </section>
      <room-info
        v-if="tabview === 'group'"
        :room="room"
        :image.sync="image"
        :isLeader="isLeader"
        @update="update"
      ></room-info>

      <participants-info
        v-else
        :participants="memberList"
        :isLeader="isLeader"
        :sessionId="sessionId"
        @kickout="kickout"
      ></participants-info>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import {
  getRoomInfo,
  updateRoomInfo,
  updateRoomProfile,
  kickoutMember,
} from 'api/workspace'
import RoomInfo from '../partials/ModalRoomInfo'
import ParticipantsInfo from '../partials/ModalParticipantsInfo'
import Profile from 'Profile'
import confirmMixin from 'mixins/confirm'

export default {
  name: 'WorkspaceRoomInfo',
  mixins: [confirmMixin],
  components: {
    Modal,
    Profile,
    RoomInfo,
    ParticipantsInfo,
  },
  data() {
    return {
      room: null,
      tabview: 'group',
      visibleFlag: false,
      image: null,
    }
  },
  computed: {
    memberList() {
      if (this.room) {
        return this.room.memberList
      } else {
        return []
      }
    },
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    sessionId: {
      type: String,
      required: true,
    },
    isLeader: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    visible(flag) {
      if (flag === true) {
        this.initRemote()
      }
      this.visibleFlag = flag
    },
  },
  methods: {
    async initRemote() {
      try {
        this.room = await getRoomInfo({
          sessionId: this.sessionId,
          workspaceId: this.workspace.uuid,
        })
        this.image = this.room.profile
        this.tabview = 'group'
      } catch (err) {
        console.error(err)
      }
    },
    tabChange(view) {
      this.tabview = view
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
    async update(params) {
      try {
        if ('image' in params && params['image'] !== null) {
          await updateRoomProfile({
            profile: params.image,
            sessionId: params.sessionId,
            uuid: this.account.uuid,
            workspaceId: this.workspace.uuid,
          })
          delete params['image']
        }
        const updateRtn = await updateRoomInfo(params)
        if (updateRtn) {
          this.$emit('updatedInfo', params)
          this.initRemote()
          // this.$emit('update:visible', false)
        }
      } catch (err) {
        // 에러처리
        console.error(err)
      }
    },
    kickoutConfirm(id) {
      this.confirmCancel(
        this.$t('confirm.access_remove'),
        {
          text: this.$t('button.confirm'),
          action: () => {
            this.kickout(id)
          },
        },
        {
          text: this.$t('button.cancel'),
        },
      )
    },
    async kickout(id) {
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
          const idx = this.memberList.findIndex(member => member.uuid === id)
          if (idx < 0) return
          this.memberList.splice(idx, 1)
          this.$emit('updatedInfo', {})
        }
      } catch (err) {
        console.error(err)
      }
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/workspace/workspace-roominfo.scss"></style>

<style lang="scss">
@import '~assets/style/mixin';
.modal.modal-roominfo {
  .modal--body {
    padding: 0;
  }

  .modal--inner {
    display: flex;
    flex-direction: column;
    @include modal();
  }
  .modal--header {
    flex: 0;
  }

  .modal--body {
    flex: 1;
  }
}
</style>
