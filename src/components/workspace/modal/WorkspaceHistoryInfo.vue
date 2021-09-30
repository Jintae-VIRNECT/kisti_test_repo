<template>
  <div>
    <modal
      :title="$t('workspace.info_remote_detail')"
      width="64.286em"
      height="56.429em"
      :showClose="true"
      :visible.sync="visiblePcFlag"
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
          </div>
        </section>
        <room-info
          v-if="tabview === 'group' && room && room.title"
          :room="room"
        ></room-info>

        <participants-info
          v-else-if="tabview === 'user'"
          :participants="participants"
          :sessionId="sessionId"
        ></participants-info>
      </div>
    </modal>

    <!-- 모바일 레이아웃 -->
    <workspace-mobile-room-info
      :room="room"
      :sessionId="sessionId"
      :visible.sync="visibleMobileFlag"
      :beforeClose="beforeClose"
      :tabview="tabview"
      :memberList="participants"
      :isHistory="true"
      @tabChange="tabChange"
    >
    </workspace-mobile-room-info>
  </div>
</template>

<script>
import Modal from 'Modal'
import { getHistorySingleItem } from 'api/http/history'
import RoomInfo from '../partials/ModalHistoryRoomInfo'
import ParticipantsInfo from '../partials/ModalParticipantsInfo'
import Profile from 'Profile'
import responsiveModalVisibleMixin from 'mixins/responsiveModalVisible'

export default {
  name: 'WorkspaceHistoryInfo',
  components: {
    Modal,
    Profile,
    RoomInfo,
    ParticipantsInfo,
    WorkspaceMobileRoomInfo: () => import('./WorkspaceMobileRoomInfo'),
  },
  mixins: [responsiveModalVisibleMixin],
  data() {
    return {
      room: null,
      tabview: 'group',
      //visibleFlag: false,
      image: null,
    }
  },
  computed: {
    participants() {
      if (this.room) {
        return this.room.memberList
      } else return []
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
  },
  watch: {
    visible(flag) {
      if (flag === true) {
        this.initHistory()
      }
      //this.visibleFlag = flag
      this.setVisiblePcOrMobileFlag(flag)
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
