import {
  joinRoom,
  createRoom,
  restartRoom,
  updateRoomProfile,
  getRoomInfo,
} from 'api/http/room'
import { ROLE } from 'configs/remote.config'
import { DEVICE } from 'configs/device.config'
import { ROOM_STATUS } from 'configs/status.config'
import { ERROR } from 'configs/error.config'
import { URLS } from 'configs/env.config'

import toastMixin from 'mixins/toast'
import callMixin from 'mixins/call'
import errorMsgMixin from 'mixins/errorMsg'
import confirmMixin from 'mixins/confirm'

import auth, { isRegisted } from 'utils/auth'
import { mapActions, mapGetters } from 'vuex'

import { joinOpenRoomAsGuest } from 'api/http/guest'

export default {
  mixins: [toastMixin, callMixin, errorMsgMixin, confirmMixin],
  data() {
    return {
      clicked: false,
    }
  },
  computed: {
    ...mapGetters(['targetCompany', 'restrictedMode', 'useScreenStrict']),
  },
  methods: {
    ...mapActions(['roomClear', 'setRoomInfo']),
    async join(room, showRestrictAgreeModal = true) {
      this.logger('>>> JOIN ROOM')

      try {
        //멤버 상태 등록 안된 경우 협업방 입장 불가
        //그러나 guest 멤버는 체크하지 않음.

        if (!isRegisted && !room.isGuest) {
          this.toastDefault(this.$t('workspace.auth_status_failed'))
          return
        }

        if (this.clicked === true) return
        this.clicked = true

        //@TODO - ROLE.GUEST 재정의
        let role
        if (room.sessionType === ROOM_STATUS.PRIVATE) {
          const myInfo = room.memberList.find(
            member => member.uuid === this.account.uuid,
          )
          if (myInfo === undefined) throw Error('not allow to participant')
          role = myInfo.memberType === ROLE.LEADER ? ROLE.LEADER : ROLE.EXPERT
        } else {
          if (this.account.roleType === ROLE.GUEST) {
            role = ROLE.GUEST
          } else {
            role =
              room.leaderId === this.account.uuid ? ROLE.LEADER : ROLE.EXPERT
          }
        }

        const showVideoRestrictModal =
          role !== ROLE.LEADER &&
          showRestrictAgreeModal &&
          room.videoRestrictedMode

        if (showVideoRestrictModal) {
          this.showVideoRestrictMessage({ room, role })
        } else {
          return await this.doJoin(room, role)
        }
      } catch (err) {
        this.handleJoinError({ err, room })
      }
    },
    showVideoRestrictMessage({ room, role }) {
      this.connectCancel(
        this.$t('workspace.confirm_video_restrict_join'),
        {
          text: this.$t('button.connect'),
          action: () => {
            this.doJoin(room, role)
          },
        },
        {
          text: this.$t('button.cancel'),
          action: () => {
            this.clicked = false
          },
        },
      )
    },
    async doJoin(room, role) {
      try {
        this.$eventBus.$emit('roomloading:show', {
          toggle: true,
          isOpenRoom: room.sessionType === 'OPEN' ? true : false,
          isJoin: true,
        })

        const options = await this.getDeviceId()
        const mediaStream = await this.$call.getStream(options)

        const getJoinParams = () => {
          return {
            uuid: this.account.uuid,
            deviceType: DEVICE.WEB,
            sessionId: room.sessionId,
            workspaceId: this.workspace.uuid,
          }
        }

        let res = null
        if (room.isGuest) {
          res = await joinOpenRoomAsGuest({
            ...getJoinParams(),
            memberType: ROLE.GUEST,
          })
        } else {
          res = await joinRoom({
            ...getJoinParams(),
            memberType: role,
          })
        }

        this.setRoomInfo({
          ...room,
          audioRestrictedMode: res.audioRestrictedMode,
          videoRestrictedMode: res.videoRestrictedMode,
        })

        const joinRtn = await this.$call.connect(
          res,
          role,
          options,
          mediaStream,
        )

        if (joinRtn) {
          this.$nextTick(() => {
            this.$router.push({ name: 'service' })
          })
          return true
        } else {
          this.roomClear()
          console.error('>>>join room fail')
          this.clicked = false
        }
      } catch (err) {
        this.handleJoinError({ err, room })
      }
    },

    //원격 협업 시작
    //info : {title, description, imageFile, imageUrl, open}
    async startRemote(info) {
      try {
        const isOpenRoom = info.open

        //멤버 상태 등록 안된 경우 협업방 입장 불가
        if (!isRegisted) {
          this.toastDefault(this.$t('workspace.auth_status_failed'))
          return
        }

        if (this.clicked === true) return
        this.clicked = true

        //loading ui 표사
        this.$eventBus.$emit('roomloading:show', { toggle: true, isOpenRoom })

        const options = await this.getDeviceId() //callMixin
        const mediaStream = await this.$call.getStream(options)

        //참여자 정보
        let selectedUser = []
        let selectedUserIds = []

        if (!isOpenRoom) {
          for (let select of this.selection) {
            selectedUser.push({
              id: select.uuid,
              uuid: select.uuid,
              email: select.email,
            })
            selectedUserIds.push(select.uuid)
          }
        }

        let createdRes

        const getRoomParams = () => {
          return {
            client: 'DESKTOP',
            userId: this.account.uuid,
            title: info.title,
            description: info.description,
            leaderId: this.account.uuid,
            participantIds: selectedUserIds,
            workspaceId: this.workspace.uuid,
            companyCode: this.targetCompany,
            videoRestrictedMode:
              this.restrictedMode.video && this.useScreenStrict,
            audioRestrictedMode:
              this.restrictedMode.audio && this.useScreenStrict,
          }
        }

        //sessionId : props
        const isCreateRoom =
          this.sessionId &&
          this.sessionId.length > 0 &&
          info.imageUrl &&
          info.imageUrl !== 'default'

        if (isCreateRoom) {
          createdRes = await restartRoom({
            ...getRoomParams(),
            sessionId: this.sessionId,
            sessionType: isOpenRoom ? ROOM_STATUS.OPEN : ROOM_STATUS.PRIVATE,
          })
        } else {
          createdRes = await createRoom({
            ...getRoomParams(),
            sessionType: isOpenRoom ? ROOM_STATUS.OPEN : ROOM_STATUS.PRIVATE,
          })
        }

        if (info.imageFile) {
          const res = await updateRoomProfile({
            profile: info.imageFile,
            sessionId: createdRes.sessionId,
            uuid: this.account.uuid,
            workspaceId: this.workspace.uuid,
          })

          if (res.usedStoragePer >= 90) {
            this.toastError(this.$t('alarm.file_storage_about_to_limit'))
          }
        }

        const connRes = await this.$call.connect(
          createdRes,
          ROLE.LEADER,
          options,
          mediaStream,
        )

        //api
        const roomInfo = await getRoomInfo({
          sessionId: createdRes.sessionId,
          workspaceId: this.workspace.uuid,
        })

        //vuex action
        this.setRoomInfo({
          ...roomInfo,
          leaderId: this.account.uuid,
          open: isOpenRoom,
          videoRestrictedMode: createdRes.videoRestrictedMode,
          audioRestrictedMode: createdRes.audioRestrictedMode,
        })

        if (connRes) {
          this.clicked = false
          this.$eventBus.$emit('popover:close')

          this.$nextTick(() => {
            this.$router.push({ name: 'service' })
          })
        } else {
          this.roomClear() //vuex action
          console.error('join room fail')
          this.clicked = false
        }
      } catch (err) {
        this.handleRoomCreateError(err)
      }
    },

    /**
     * 협업 생성(재시작)시 발생하는 에러 처리
     * @param {Error} err
     */
    handleRoomCreateError(err) {
      this.clicked = false
      this.$eventBus.$emit('roomloading:show', { toggle: false })
      this.roomClear() //vuex action
      if (typeof err === 'string') {
        this.handleDeviceError(err)
      } else if (err.code === 7003) {
        this.toastError(this.$t('service.file_type'))
      } else if (err.code === 7004) {
        this.toastError(this.$t('service.file_maxsize'))
      } else if (err.code === 7017) {
        this.toastError(this.$t('alarm.file_storage_capacity_full'))
      } else {
        console.error(`${err.message} (${err.code})`)
        if (err.code) {
          this.toastError(this.$t('confirm.network_error'))
        }
      }
    },

    /***
     * 협업 참여시 발생하는 에러 처리
     * @param {Error}} err 에러 객체
     * @param {Object} room room 정보
     */
    handleJoinError({ err, room }) {
      this.clicked = false
      this.$eventBus.$emit('roomloading:show', { toggle: false })
      this.roomClear()

      // WorkspaceRemote.vue의 init을 호출하기 위함. 솔직히 좋은 패턴은 아닌듯
      if (this['init'] && typeof this['init'] === 'function') {
        this.init()
      }

      if (typeof err === 'string') {
        this.handleDeviceError(err)
        return
      } else {
        console.error(`${err.message} (${err.code})`)
        if (err.code === ERROR.REMOTE_ALREADY_REMOVED) {
          this.showErrorToast(err.code)
          return false
        } else if (err.code === ERROR.REMOTE_ALREADY_INVITE) {
          this.showErrorToast(err.code)
          return false
        } else if (err.code === 4021) {
          if (room.isGuest) {
            this.confirmDefault(this.$t('alarm.invite_fail_maxuser'), {
              action: async () => {
                await auth.logout(false)
                location.href = `${URLS['console']}`
              },
            })
          } else {
            this.toastError(
              this.$t('workspace.remote_access_overflow', {
                num: room.maxUserCount,
              }),
            )
          }

          return false
        } else if (err.code === ERROR.MEMBER_UUID_IS_INVALID) {
          this.confirmDefault(
            this.$t('guest.guest_account_deleted_and_request_account_again'),
            {
              action: async () => {
                const reason = 'deleted'
                this.$eventBus.$emit('initTimerAndGuestMember', reason)
              },
              text: this.$t('button.request_again'),
            },
          )
        }
      }
      this.toastError(this.$t('workspace.remote_invite_impossible'))
    },

    /**
     * 장치 관련 에러 처리
     * @param {Error} err 에러 객체
     */
    handleDeviceError(err) {
      console.error(err)
      if (err === 'nodevice') {
        this.toastError(this.$t('workspace.error_no_connected_device'))
      } else if (err.toLowerCase() === 'requested device not found') {
        this.toastError(this.$t('workspace.error_no_device'))
      } else if (err.toLowerCase() === 'device access deined') {
        this.$eventBus.$emit('devicedenied:show')
      }
    },
  },
}
