<template>
  <full-screen-modal
    :title="$t('workspace.info_remote_detail')"
    :visible="visible"
    :border="false"
    @close="close"
  >
    <div class="workspace-mobile-roominfo">
      <section class="roominfo-nav">
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
      <keep-alive>
        <room-info
          v-if="tabview === 'group'"
          :room="room"
          :image.sync="mobileProfileImage"
          :isLeader="isLeader"
          :isHistory="isHistory"
          @update="update"
        ></room-info>

        <participants-info
          v-else-if="tabview === 'user'"
          :participants="memberList"
          :isLeader="isLeader"
          :sessionId="sessionId"
          @kickout="kickout"
        ></participants-info>
      </keep-alive>
    </div>
  </full-screen-modal>
</template>

<script>
import RoomInfo from '../partials/ModalRoomInfo'
import ParticipantsInfo from '../partials/ModalParticipantsInfo'
import Header from '../../header/Header.vue'
import FullScreenModal from 'FullScreenModal'
export default {
  components: {
    RoomInfo,
    ParticipantsInfo,
    FullScreenModal,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    beforeClose: {
      type: Function,
    },
    sessionId: {
      type: String,
      required: true,
    },
    isLeader: {
      type: Boolean,
      default: false,
    },
    room: {
      type: Object,
      default: null,
    },
    image: {
      type: String,
      default: '',
    },
    tabview: {
      type: String,
      default: 'group',
    },
    memberList: {
      type: Array,
    },
    isHistory: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      mobileProfileImage: 'default',
    }
  },
  methods: {
    close() {
      this.beforeClose()
    },
    update(params) {
      this.$emit('update', params)
    },
    tabChange(tabView) {
      this.$emit('tabChange', tabView)
    },
    kickout(id) {
      this.$emit('kickout', id)
    },
  },
  mounted() {
    this.mobileProfileImage = this.image
  },
}
Header
</script>

<style></style>
