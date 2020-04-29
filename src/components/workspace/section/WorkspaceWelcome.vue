<template>
  <section class="workspace-welcome">
    <div class="workspace-welcome__body offsetwidth">
      <p class="workspace-welcome__group">
        리모트원격솔루션 <role :role="'Manager'"></role>
      </p>
      <p class="workspace-welcome__name">리모트님, 반갑습니다.</p>
      <button class="btn" @click="createRoom">원격 협업 생성</button>
    </div>
    <create-room-modal :visible.sync="visible"></create-room-modal>
  </section>
</template>

<script>
import Role from 'Role'
import CreateRoomModal from '../modal/WorkspaceCreateRoom'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
export default {
  name: 'WorkspaceWelcome',
  mixins: [toastMixin, confirmMixin],
  components: {
    Role,
    CreateRoomModal,
  },
  data() {
    return {
      visible: false,
    }
  },
  computed: {},
  watch: {},
  methods: {
    createRoom() {
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
