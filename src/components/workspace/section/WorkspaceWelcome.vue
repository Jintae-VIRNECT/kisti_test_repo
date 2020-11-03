<template>
  <section class="workspace-welcome">
    <div
      class="workspace-welcome__body offsetwidth"
      :class="{ empty: emptyWorkspace }"
    >
      <div class="workspace-welcome__group" v-if="!emptyWorkspace">
        <p>
          {{ workspace.title }}
        </p>
        <role v-if="showRole" :role="workspace.role"></role>
        <role
          v-if="hasLicense && expireLicense"
          :role="$t('workspace.expire_license')"
          :opt="'expired'"
        ></role>
      </div>
      <div class="workspace-welcome__name">
        <!-- <span class="workspace-welcome__nickname">{{ account.nickname }}</span> -->
        <span class="workspace-welcome__text" v-html="welcomeText"></span>
      </div>
      <button
        v-if="!emptyWorkspace && !expireLicense"
        class="btn"
        @click="createRoom"
      >
        {{ $t('workspace.create_room') }}
      </button>
      <button
        v-if="useOpenRoom && !emptyWorkspace && !expireLicense"
        class="btn workspace-welcome__open"
        @click="createOpenRoom"
      >
        {{ $t('workspace.create_open_room') }}
      </button>
    </div>
    <create-room-modal :visible.sync="visible"></create-room-modal>
    <open-room-modal :visible.sync="openVisible"></open-room-modal>
  </section>
</template>

<script>
import Role from 'Role'
import CreateRoomModal from '../modal/WorkspaceCreateRoom'
import OpenRoomModal from '../modal/WorkspaceCreateOpenRoom'
import { mapGetters } from 'vuex'
import { WORKSPACE_ROLE } from 'configs/status.config'
export default {
  name: 'WorkspaceWelcome',
  components: {
    Role,
    CreateRoomModal,
    OpenRoomModal,
  },
  data() {
    return {
      visible: false,
      openVisible: false,
    }
  },
  computed: {
    ...mapGetters(['expireLicense', 'useOpenRoom']),
    emptyWorkspace() {
      if (!this.hasLicense || !(this.workspace && this.workspace.uuid)) {
        return true
      } else {
        return false
      }
    },
    welcomeText() {
      if (!this.inited) {
        return ''
      }
      if (this.hasLicense && !this.expireLicense) {
        return this.$t('workspace.welcome', { name: this.account.nickname })
      } else if (!this.hasLicense) {
        return this.$t('workspace.welcome_license', {
          name: this.account.nickname,
        })
      } else {
        return this.$t('workspace.welcome_expire', {
          name: this.account.nickname,
        })
      }
    },
    showRole() {
      if (this.workspace.role === WORKSPACE_ROLE.MEMBER) {
        return false
      } else {
        return true
      }
    },
  },
  props: {
    inited: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    async createRoom() {
      this.visible = !this.visible
    },
    createOpenRoom() {
      this.openVisible = !this.openVisible
    },
  },

  /* Lifecycles */
  beforeDestroy() {
    this.$eventBus.$off('openCreateRoom')
  },
  created() {
    this.$eventBus.$on('openCreateRoom', this.createRoom)
  },
}
</script>
