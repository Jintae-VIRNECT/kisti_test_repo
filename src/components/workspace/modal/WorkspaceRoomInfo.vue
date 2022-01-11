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
            <!-- <button
              v-if="useStorage"
              class="roominfo-nav__menu"
              :class="{ active: tabview === 'download' }"
              :data-text="$t('button.download')"
              @click="tabChange('download')"
            >
              {{ $t('button.download') }}
            </button> -->
          </div>
        </section>

        <keep-alive>
          <room-info
            v-if="tabview === 'group'"
            :room="room"
            :image.sync="image"
            :isLeader="isLeader"
            @update="update"
          ></room-info>

          <participants-info
            v-else-if="tabview === 'user'"
            :participants="memberList"
            :isLeader="isLeader"
            :sessionId="sessionId"
            @kickout="kickout"
          ></participants-info>
          <!-- <room-download v-else :sessionId="sessionId"></room-download> -->
        </keep-alive>
      </div>
    </modal>

    <!-- 모바일 레이아웃 -->
    <workspace-mobile-room-info
      :room="room"
      :sessionId="sessionId"
      :isLeader="isLeader"
      :visible.sync="visibleMobileFlag"
      :beforeClose="beforeClose"
      :tabview="tabview"
      :memberList="memberList"
      :image.sync="image"
      @tabChange="tabChange"
      @update="update"
      @kickout="kickout"
    >
    </workspace-mobile-room-info>
  </div>
</template>

<script>
import Modal from 'Modal'
import {
  getRoomInfo,
  updateRoomInfo,
  removeRoomProfile,
  updateRoomProfile,
} from 'api/http/room'
import { kickoutMember } from 'api/http/member'
import RoomInfo from '../partials/ModalRoomInfo'
import ParticipantsInfo from '../partials/ModalParticipantsInfo'
// import RoomDownload from '../partials/ModalRoomDownload'
import Profile from 'Profile'

import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
import errorMsgMixin from 'mixins/errorMsg'
import responsiveModalVisibleMixin from 'mixins/responsiveModalVisible'

import { ERROR } from 'configs/error.config'

import { mapGetters } from 'vuex'

export default {
  name: 'WorkspaceRoomInfo',
  mixins: [
    toastMixin,
    confirmMixin,
    errorMsgMixin,
    responsiveModalVisibleMixin,
  ],
  components: {
    Modal,
    Profile,
    RoomInfo,
    ParticipantsInfo,
    // RoomDownload,
    WorkspaceMobileRoomInfo: () => import('./WorkspaceMobileRoomInfo'),
  },
  data() {
    return {
      room: null,
      tabview: 'group',
      image: null,
    }
  },
  computed: {
    ...mapGetters(['useStorage']),
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
      this.setVisiblePcOrMobileFlag(flag)
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
        if (err.code === ERROR.REMOTE_ALREADY_REMOVED) {
          this.showErrorToast(err.code)
          this.$emit('update:visible', false)
        }
      }
    },
    tabChange(view) {
      this.tabview = view
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
    async update(params) {
      const isUpdate =
        'image' in params &&
        params['image'] !== null &&
        params['image'] !== 'default'

      const isDelete =
        'image' in params &&
        this.room.profile !== 'default' &&
        (params['image'] === 'default' || !params['image'])

      try {
        if (isUpdate) {
          await this.updateProfile(params)
          delete params['image']
        } else if (isDelete) {
          await this.deleteProfile(params)
        }
        const updateRtn = await updateRoomInfo(params)
        if (updateRtn) {
          this.$emit('updatedInfo', params)
          this.$emit('update:visible', false)
        }
      } catch (err) {
        if (err.code === ERROR.REMOTE_ALREADY_REMOVED) {
          this.showErrorToast(err.code)
          this.$emit('update:visible', false)
        } else if (err.code === ERROR.FILE_STORAGE_CAPACITY_FULL) {
          this.showErrorToast(err.code)
        }
      }
    },
    async updateProfile(params) {
      const profile = await updateRoomProfile({
        profile: params.image,
        sessionId: params.sessionId,
        uuid: this.account.uuid,
        workspaceId: this.workspace.uuid,
      })

      if (profile.usedStoragePer >= 90) {
        this.toastError(this.$t('alarm.file_storage_about_to_limit'))
      }

      this.$emit('updatedInfo', profile)
    },
    async deleteProfile(params) {
      await removeRoomProfile({
        sessionId: params.sessionId,
        workspaceId: this.workspace.uuid,
      })
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
        if (err.code === ERROR.REMOTE_ALREADY_REMOVED) {
          this.showErrorToast(err.code)
          this.$emit('update:visible', false)
        }
      }
    },
  },

  /* Lifecycles */
  mounted() {
    this.$eventBus.$on('close:roominfo', this.beforeClose)
  },
  beforeDestroy() {
    this.$eventBus.$off('close:roominfo', this.beforeClose)
  },
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
