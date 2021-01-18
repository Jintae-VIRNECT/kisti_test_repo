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
      <div class="workspace-welcome__name" v-html="welcomeText"></div>
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
      return !this.hasLicense || !(this.workspace && this.workspace.uuid)
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
      return (
        this.workspace.role === WORKSPACE_ROLE.MASTER ||
        this.workspace.role === WORKSPACE_ROLE.MANAGER
      )
    },
  },
  props: {
    inited: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    createRoom() {
      this.visible = !this.visible
    },
    createOpenRoom() {
      this.openVisible = !this.openVisible
    },
  },

  /* Lifecycles */
  beforeDestroy() {
    this.$eventBus.$off('open:modal:create')
    this.$eventBus.$off('open:modal:createOpen')
  },
  created() {
    this.$eventBus.$on('open:modal:create', this.createRoom)
    this.$eventBus.$on('open:modal:createOpen', this.createOpenRoom)
  },
}
</script>
