<template>
  <modal
    title="협업 참가 요청"
    width="712px"
    height="842px"
    :showClose="true"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    customClass="service-invite"
  >
    <div class="invite-modal__body">
      <div v-if="!nouser" class="invite-modal__selected">
        <p class="invite-modal__selected-title">선택한 멤버 (3/4)</p>
        <profile-list
          v-if="selection.length > 0"
          :users="selection"
          size="3.714em"
        ></profile-list>
        <p class="invite-modal__selected-empty" v-else>
          멤버를 추가해 주세요.
        </p>
      </div>
      <room-invite
        :users="users"
        :selection="selection"
        @userSelect="selectUser"
        @inviteRefresh="inviteRefresh"
      ></room-invite>
      <div class="invite-modal__footer">
        <button class="btn" :disabled="selection.length === 0">
          협업 요청
        </button>
      </div>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'
import ProfileList from 'ProfileList'
import RoomInvite from 'components/workspace/partials/ModalCreateRoomInvite'

import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'
export default {
  name: 'ServiceInviteModal',
  mixins: [toastMixin, confirmMixin],
  components: {
    Modal,
    ProfileList,
    RoomInvite,
  },
  data() {
    return {
      selection: [],
      nouser: false,
      visibleFlag: false,
      users: [
        {
          createdDate: '2020-04-17T03:54:09',
          description: '테스트 유저 입니다.',
          email: 'test2@test.com',
          joinDate: '2020-05-12T06:42:36',
          licenseProducts: Array(0),
          loginLock: 'INACTIVE',
          name: '홍길동2',
          nickName: '왕밤빵2',
          profile:
            'https://virnect-platform-qa.s3.ap-northeast-2.amazonaws.com/profile/2020-06-01_944a433217aa4c7d93f27b9f4ffef223png',
          role: 'MEMBER',
          roleId: 3,
          updatedDate: '2020-06-01T01:13:34',
          userType: 'USER',
          uuid: '4b260e69bd6fa9a583c9bbe40f5aceb3',
        },
      ],
      maxSelect: 2,
      roomInfo: {},
    }
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    visible(flag) {
      if (flag) {
        this.inviteRefresh()
      }
      this.visibleFlag = flag
    },
  },
  methods: {
    reset() {
      this.selection = []
    },
    beforeClose() {
      this.$emit('update:visible', false)
    },
    selectUser(user) {
      const idx = this.selection.findIndex(select => user.uuid === select.uuid)
      if (idx < 0) {
        if (this.selection.length >= this.maxSelect) {
          this.toastNotify('최대 2명까지 선택이 가능합니다.')
          return
        }
        this.selection.push(user)
      } else {
        this.selection.splice(idx, 1)
      }
    },
    async inviteRefresh() {
      this.selection = []
    },
  },

  /* Lifecycles */
  created() {},
  mounted() {},
}
</script>

<style lang="scss" src="assets/style/service/service-invite-modal.scss"></style>
