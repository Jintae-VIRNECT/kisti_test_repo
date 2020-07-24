<template>
  <modal
    title="원격협업 상세보기"
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
          협업 정보
        </button>
        <button
          class="roominfo-nav__menu"
          :class="{ active: tabview === 'user' }"
          @click="tabChange('user')"
        >
          참가자 정보
        </button>
      </section>
      <room-info
        v-if="tabview === 'group'"
        :room="room"
        :image.sync="image"
        :leader="leader"
      ></room-info>

      <participants-info
        v-else
        :participants="participants"
        :leader="leader"
        :roomId="roomId"
      ></participants-info>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import { getHistorySingleItem } from 'api/workspace/history'
import RoomInfo from '../partials/ModalRoomInfo'
import ParticipantsInfo from '../partials/ModalParticipantsInfo'
import Profile from 'Profile'

export default {
  name: 'WorkspaceHistoryInfo',
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
    participants() {
      if (this.history) {
        return this.history.memberList
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
    roomId: {
      type: Number,
      // required: true,
    },
    sessionId: {
      type: String,
      required: true,
    },
    leader: {
      type: Boolean,
      default: false,
    },
    history: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    visible(flag) {
      if (flag === true) {
        this.initHistory()
      }
      this.visibleFlag = flag
    },
    image(image) {
      console.log(image)
    },
  },
  methods: {
    async initHistory() {
      try {
        this.room = await getHistorySingleItem({
          workspaceId: this.workspace.uuid,
          sessionId: this.sessionId,
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
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/workspace/workspace-roominfo.scss"></style>

<style lang="scss">
.modal.modal-roominfo {
  .modal--body {
    padding: 0;
  }

  .modal--inner {
    display: flex;
    flex-direction: column;
    @media screen and (max-width: 900px), (max-height: 790px) {
      font-size: 12px;
    }
  }
  .modal--header {
    flex: 0;
  }

  .modal--body {
    flex: 1;
  }
}
</style>
