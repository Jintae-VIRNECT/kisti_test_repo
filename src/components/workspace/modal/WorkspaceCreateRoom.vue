<template>
  <div class="workspace-createroom-wrapper">
    <modal
      :title="$t('workspace.create_remote')"
      width="78.429em"
      :showClose="true"
      :visible.sync="visibleFlag"
      :beforeClose="beforeClose"
      customClass="createroom-modal"
    >
      <div class="createroom">
        <create-room-info
          :key="'roominfo_' + sessionId"
          :roomInfo="roomInfo"
          :selection="selection"
          :btnLoading="clicked"
          @startRemote="startRemote"
        ></create-room-info>
        <create-room-invite
          :users="users"
          :selection="selection"
          @userSelect="selectUser"
          @inviteRefresh="inviteRefresh"
          :loading="loading"
        ></create-room-invite>
      </div>
    </modal>
    <workspace-mobile-create-room
      :visible.sync="visibleFlag"
      :btnLoading="clicked"
      :roomInfo="roomInfo"
      :users="users"
      :selection="selection"
      :beforeClose="beforeClose"
      :loading="loading"
      @userSelect="selectUser"
      @inviteRefresh="inviteRefresh"
      @startRemote="startRemote"
    ></workspace-mobile-create-room>
  </div>
</template>

<script>
import Modal from 'Modal'
import CreateRoomInfo from '../partials/ModalCreateRoomInfo'
import CreateRoomInvite from '../partials/ModalCreateRoomInvite'
import createRoomMixin from 'mixins/createRoom'
import { maxParticipants } from 'utils/callOptions'
import { mapGetters } from 'vuex'

export default {
  name: 'WorkspaceCreateRoom',
  mixins: [createRoomMixin],
  components: {
    Modal,
    CreateRoomInfo,
    CreateRoomInvite,
    WorkspaceMobileCreateRoom: () => import('./WorkspaceMobileCreateRoom'),
  },
  props: {
    sessionId: {
      type: String,
      default: '',
    },
    visible: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      clicked: false,
      selection: [],
      selectHistory: [],
      users: [],
      loading: false,
      visibleFlag: false,
      roomInfo: {},
      maxSelect: maxParticipants - 1,
    }
  },
  computed: {
    ...mapGetters(['restrictedMode', 'targetCompany', 'useScreenStrict']),
  },
  methods: {
    reset() {
      this.selection = []
      this.selectHistory = []
    },
  },
}
</script>

<style
  lang="scss"
  src="assets/style/workspace/workspace-createroom.scss"
></style>
