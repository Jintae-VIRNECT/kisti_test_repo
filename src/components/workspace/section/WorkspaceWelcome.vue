<template>
  <section class="workspace-welcome">
    <div class="workspace-welcome__body offsetwidth">
      <p class="workspace-welcome__group">
        리모트원격솔루션 <role :role="'Manager'"></role>
        <role v-if="!license" :role="'라이선스 만료'" :opt="'expired'"></role>
      </p>
      <p class="workspace-welcome__name" v-html="welcomeText"></p>
      <button v-if="license" class="btn" @click="createRoom">
        원격 협업 생성
      </button>
    </div>
    <create-room-modal :visible.sync="visible"></create-room-modal>
    <device-denied :visible.sync="showDenied"></device-denied>
  </section>
</template>

<script>
import Role from 'Role'
import CreateRoomModal from '../modal/WorkspaceCreateRoom'
import DeviceDenied from 'components/workspace/modal/WorkspaceDeviceDenied'
import { getPermission } from 'utils/deviceCheck'
export default {
  name: 'WorkspaceWelcome',
  components: {
    Role,
    CreateRoomModal,
    DeviceDenied,
  },
  data() {
    return {
      visible: false,
      showDenied: false,
    }
  },
  props: {
    license: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    welcomeText() {
      if (this.license) {
        return `${this.account.nickname} 님, 반갑습니다.`
      } else {
        return `${this.account.nickname} 님, <br />
        라이선스가 만료되었습니다.`
      }
    },
  },
  watch: {},
  methods: {
    async createRoom() {
      if (this.license) {
        this.visible = !this.visible

        const permission = await getPermission()
        if (!permission && this.visible === true) {
          this.showDenied = true
        }
      }
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
