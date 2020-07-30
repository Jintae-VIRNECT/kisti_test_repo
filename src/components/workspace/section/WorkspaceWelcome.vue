<template>
  <section class="workspace-welcome">
    <div class="workspace-welcome__body offsetwidth">
      <p class="workspace-welcome__group">
        {{ workspace.title }}
        <role v-if="showRole" :role="workspace.role"></role>
        <role
          v-if="hasLicense && expireLicense"
          role="라이선스 만료"
          :opt="'expired'"
        ></role>
      </p>
      <p class="workspace-welcome__name" v-html="welcomeText"></p>
      <button
        v-if="hasLicense && !expireLicense"
        class="btn"
        @click="createRoom"
      >
        원격 협업 생성
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
        return `${this.account.nickname} 님, 반갑습니다.`
      } else if (!this.hasLicense) {
        return `${this.account.nickname} 님, <br />
        할당된 라이선스가 없습니다.`
      } else {
        return `${this.account.nickname} 님, <br />
        라이선스가 만료되었습니다.`
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
