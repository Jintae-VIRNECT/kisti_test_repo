<template>
  <modal
    title="원격협업 상세보기"
    :width="900"
    :height="790"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    customClass="modal-roominfo"
  >
    <div class="roominfo">
      <section class="roominfo-nav">
        <img class="roominfo-nav__image" :src="image" />
        <button
          class="roominfo-nav__menu"
          :class="{ active: tabview === 'group' }"
          @click="tabChange('group')"
        >
          협업 정보
        </button>
        <button
          class="roominfo-nav__menu"
          :class="{ active: tabview === 'user' }"
          @click="tabChange('user')"
        >
          참가자 정보
        </button>
      </section>
      <room-info
        v-if="tabview === 'group'"
        :room="room"
        :image.sync="image"
        :leader="leader"
        @update="update"
      ></room-info>

      <participants-info
        v-else
        :participants="participants"
        :leader="leader"
        :roomId="roomId"
      ></participants-info>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import { getRoomInfo, updateRoomInfo } from 'api/remote/room'
import RoomInfo from '../partials/ModalRoomInfo'
import ParticipantsInfo from '../partials/ModalParticipantsInfo'

export default {
  name: 'WorkspaceRoomInfo',
  components: {
    Modal,
    RoomInfo,
    ParticipantsInfo,
  },
  data() {
    return {
      room: null,
      tabview: 'group',
      visibleFlag: false,
      image: null,
    }
  },
  computed: {
    participants() {
      if (this.room) {
        return this.room.participants
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
    roomId: {
      type: String,
      required: true,
    },
    leader: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    visible(flag) {
      if (flag === true) {
        this.clear()
      }
      this.visibleFlag = flag
    },
  },
  methods: {
    async clear() {
      try {
        this.room = await getRoomInfo({ roomId: this.roomId })
        this.image = this.room.path
        this.tabview = 'group'
      } catch (err) {
        console.error(err)
      }
    },
    tabChange(view) {
      this.tabview = view
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
    async update(data) {
      try {
        const updateRtn = await updateRoomInfo({
          title: data.name,
          description: data.description,
          image: this.image,
        })
        if (updateRtn) {
          this.$emit('update:visible', false)
        }
      } catch (err) {
        // 에러처리
        console.error(err)
      }
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/workspace/workspace-roominfo.scss"></style>

<style lang="scss">
.modal.modal-roominfo {
  .modal--body {
    padding: 0;
  }

  .modal--inner {
    display: flex;
    flex-direction: column;
  }
  .modal--header {
    flex: 0;
  }

  .modal--body {
    flex: 1;
  }
}
</style>
