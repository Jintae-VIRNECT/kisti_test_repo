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
        <div class="roominfo-nav__menus">
          <button
            class="roominfo-nav__menu"
            :class="{ active: tabview === 'group' }"
            :data-text="$t('workspace.info_remote')"
            @click="tabChange('group')"
          >
            {{ $t('workspace.info_remote') }}
          </button>
          <button
            class="roominfo-nav__menu"
            :class="{ active: tabview === 'user' }"
            :data-text="$t('workspace.info_remote_member')"
            @click="tabChange('user')"
          >
            {{ $t('workspace.info_remote_member') }}
          </button>
          <!-- <button
            v-if="isOnpremise"
            class="roominfo-nav__menu"
            :class="{ active: tabview === 'download' }"
            :data-text="$t('button.download')"
            @click="tabChange('download')"
          >
            {{ $t('button.download') }}
          </button> -->
        </div>
      </section>
      <room-info
        v-if="tabview === 'group' && room && room.title"
        :room="room"
      ></room-info>

      <participants-info
        v-else-if="tabview === 'user'"
        :participants="participants"
        :leader="leader"
        :sessionId="sessionId"
      ></participants-info>

      <!-- <room-download
        v-else-if="isOnpremise"
        :sessionId="sessionId"
      ></room-download> -->
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import { getHistorySingleItem } from 'api/http/history'
import RoomInfo from '../partials/ModalHistoryRoomInfo'
import ParticipantsInfo from '../partials/ModalParticipantsInfo'
// import RoomDownload from '../partials/ModalRoomDownload'
import Profile from 'Profile'

export default {
  name: 'WorkspaceHistoryInfo',
  components: {
    Modal,
    Profile,
    RoomInfo,
    ParticipantsInfo,
    // RoomDownload,
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
      return this.room.memberList
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
    leader: {
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
  },
  methods: {
    async initHistory() {
      try {
        this.room = {}
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
@import '~assets/style/mixin';
.modal.modal-roominfo {
  .modal--body {
    padding: 0;
  }

  .modal--inner {
    display: flex;
    flex-direction: column;
    max-width: 80%;
    max-height: 90%;
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
