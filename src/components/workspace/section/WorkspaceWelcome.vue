<template>
  <section class="workspace-welcome">
    <div
      class="workspace-welcome__body offsetwidth"
      :class="{ empty: !hasLicense }"
    >
      <div class="workspace-welcome__group" v-if="hasLicense">
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
      <p class="workspace-welcome__name" v-html="welcomeText"></p>
      <button
        v-if="hasLicense && !expireLicense"
        class="btn"
        @click="createRoom"
      >
        {{ $t('workspace.create_room') }}
      </button>
    </div>
    <create-room-modal :visible.sync="visible"></create-room-modal>
  </section>
</template>

<script>
import Role from 'Role'
import CreateRoomModal from '../modal/WorkspaceCreateRoom'
import { mapGetters } from 'vuex'
import { WORKSPACE_ROLE } from 'configs/status.config'
export default {
  name: 'WorkspaceWelcome',
  components: {
    Role,
    CreateRoomModal,
  },
  data() {
    return {
      visible: false,
    }
  },
  computed: {
    ...mapGetters(['expireLicense']),
    welcomeText() {
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
  watch: {},
  methods: {
    async createRoom() {
      this.visible = !this.visible
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
