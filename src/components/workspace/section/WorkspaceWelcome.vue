<template>
  <section class="workspace-welcome">
    <div class="workspace-welcome__body offsetwidth">
      <p class="workspace-welcome__group">
        리모트원격솔루션 <role :role="'Manager'"></role>
      </p>
      <p class="workspace-welcome__name">
        {{ account.nickname }}님, 반갑습니다.
      </p>
      <button class="btn" @click="createRoom">원격 협업 생성</button>
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
  computed: {},
  watch: {},
  methods: {
    async createRoom() {
      this.visible = !this.visible

      const permission = await getPermission()
      if (!permission && this.visible === true) {
        this.showDenied = true
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
