<template>
  <popover
    trigger="click"
    placement="bottom-end"
    popperClass="popover-notice"
    width="29.571rem"
    @visible="setVisible"
  >
    <toggle-button
      :customClass="
        `header-tools__notice toggle-header ${visible ? 'visible' : ''}`
      "
      slot="reference"
      :description="$t('common.notice')"
      size="2.429rem"
      :toggle="!onPush"
      :active="active"
      :activeSrc="require('assets/image/ic_notice.svg')"
      :inactiveSrc="require('assets/image/ic_notice_off.svg')"
      @action="notice"
    ></toggle-button>

    <div>
      <div class="popover-notice__header">
        <span>{{ $t('common.notice') }}</span>
        <switcher text="Push" :value.sync="onPush">Push</switcher>
      </div>
      <div class="popover-notice__body">
        <scroller height="28.571rem" v-if="alarmList.length > 0">
          <notice-item
            v-for="(alarm, idx) of alarmList"
            :key="'alarm_' + idx"
            :type="alarm.type"
            :info="alarm.info"
            :description="alarm.description"
            :date="alarm.date"
            :filename="alarm.filename"
            :filelink="alarm.filelink"
            :image="alarm.image"
            :accept="alarm.accept"
            @accept="acceptInvite(alarm.sessionId, alarm.userId)"
            @refuse="inviteDenied(alarm.sessionId, alarm.userId)"
            @remove="remove(alarm)"
          ></notice-item>
          <!-- <notice-item
            type="file"
            :info="'Harry Ha 님'"
            :description="'파일 링크 전달드립니다.'"
            :date="'2020.01.20 오전 11:00'"
            :filename="'VIRNECT Remote WEB 2.0'"
            :filelink="'https://virnect.com'"
            :image="'default'"
          ></notice-item>
          <notice-item
            type="file"
            :info="'Harry Ha 님'"
            :description="'파일 링크 전달드립니다.'"
            :date="'2020.01.20 오전 11:00'"
            :filename="'VIRNECT Remote WEB 2.0'"
            :filelink="'https://virnect.com'"
            :image="'default'"
          ></notice-item>
          <notice-item
            type="message"
            :info="'Harry Ha 님'"
            :description="'통화요청 부탁드립니다.'"
            :date="'2020.01.20 오전 11:00'"
            :image="'default'"
          ></notice-item>
          <notice-item
            type="invite"
            :info="'Harry Ha 님'"
            :description="'참가자로 협업을 요청하였습니다.'"
            :date="'2020.01.20 오전 11:00'"
            :image="'default'"
          ></notice-item>
          <notice-item
            type="fail"
            :info="'[협업 참가 실패]'"
            :description="'최대 참가인원이 초과하였습니다.'"
            :date="'2020.01.20 오전 11:00'"
          ></notice-item>
          <notice-item
            type="license"
            :info="'[만료 안내]'"
            :description="'라이선스가 만료 되었습니다.'"
            :date="'2020.01.20 오전 11:00'"
          ></notice-item>
          <notice-item
            type="info"
            :info="'[만료 안내]'"
            :description="'라이선스 만료 <em>[60분]</em> 남았습니다.'"
            :date="'2020.01.20 오전 11:00'"
          ></notice-item> -->
        </scroller>
        <div class="popover-notice__empty" v-else>
          <div class="popover-notice__empty-box">
            <img src="~assets/image/img_noalarm.svg" />
            <span>{{ $t('alarm.no_alarm') }}</span>
          </div>
        </div>
      </div>
      <!-- <div class="popover-notice__footer">
        <span>{{ $t('alarm.saved_duration') }}</span>
      </div> -->
    </div>
    <audio preload="auto" ref="noticeAudio" playsinline muted="muted">
      <source src="~assets/media/end.mp3" />
    </audio>
  </popover>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import { EVENT } from 'configs/push.config'
import { ROLE } from 'configs/remote.config'
import { sendPush } from 'api/http/message'
import { getRoomInfo } from 'api/http/room'
import auth from 'utils/auth'
import { URLS } from 'configs/env.config'

import Switcher from 'Switcher'
import Popover from 'Popover'
import ToggleButton from 'ToggleButton'
import Scroller from 'Scroller'
import NoticeItem from './NoticeItem'

import alarmMixin from 'mixins/alarm'
import roomMixin from 'mixins/room'
import toastMixin from 'mixins/toast'
import confirmMixin from 'mixins/confirm'

export default {
  name: 'Notice',
  mixins: [roomMixin, alarmMixin, toastMixin, confirmMixin],
  components: {
    Switcher,
    Popover,
    ToggleButton,
    Scroller,
    NoticeItem,
  },
  data() {
    return {
      onPush: true,
      key: '',
      active: false,
      visible: false,
      muted: true,
      // alarmList: [],
    }
  },
  computed: {
    ...mapGetters([
      'alarmList',
      'statusSessionId', //멤버 상태 소켓에서 발급받은 session id
    ]),
  },
  watch: {
    onPush(push) {
      if (push) {
        localStorage.setItem('push', 'true')
      } else {
        localStorage.setItem('push', 'false')
      }
    },
    workspace(val, oldVal) {
      if (val.uuid && !oldVal.uuid) {
        this.pushInit()
      }
    },
  },
  methods: {
    ...mapActions([
      'addAlarm',
      'removeAlarm',
      'updateAlarm',
      'inviteResponseAlarm',
      'clearWorkspace',
    ]),
    loadeddata() {
      this.$refs['noticeAudio'].onloadeddata = () => {}
      if (this.isSafari) {
        window.addEventListener('touchstart', this.loadAudio)
      }
    },
    loadAudio() {
      this.$refs['noticeAudio'].muted = true
      window.removeEventListener('touchstart', this.loadAudio)
      this.$refs['noticeAudio'].play()
      this.$refs['noticeAudio'].pause()

      this.$refs['noticeAudio'].muted = false
    },
    setVisible(value) {
      this.visible = value
    },
    notice() {
      this.active = false
    },
    playSound() {
      this.$refs['noticeAudio'].muted = false
      this.$refs['noticeAudio'].play()
    },
    async alarmListener(listen) {
      const body = JSON.parse(listen.body)

      if (body.targetUserIds.indexOf(this.account.uuid) < 0) return
      if (
        body.event !== EVENT.FORCE_LOGOUT &&
        body.userId === this.account.uuid
      )
        return

      this.logger('received message::', body)

      switch (body.event) {
        case EVENT.INVITE:
          if (!this.onPush) return
          this.addAlarm({
            type: 'invite',
            info: this.$t('alarm.member_name_from', {
              name: body.contents.nickName,
            }),
            image: body.contents.profile,
            description: this.$t('alarm.invite_request'),
            sessionId: body.contents.sessionId,
            userId: body.userId,
            accept: 'none',
            date: new Date(),
          })
          // if (!this.onPush) return
          this.playSound()
          this.alarmInvite(
            body.contents,
            () => this.acceptInvite(body.contents.sessionId, body.userId),
            () => this.inviteDenied(body.contents.sessionId, body.userId),
          )
          break
        case EVENT.INVITE_ACCEPTED:
          if (!this.onPush) return

          this.addAlarm({
            type: 'info_user',
            info: this.$t('alarm.member_name_from', {
              name: body.contents.nickName,
            }),
            title: '',
            description: this.$t('alarm.invite_accept'),
            date: new Date(),
          })
          // if (!this.onPush) return
          this.alarmInviteAccepted(body.contents.nickName)
          break
        case EVENT.INVITE_DENIED:
          if (!this.onPush) return
          this.addAlarm({
            type: 'info_user',
            info: this.$t('alarm.member_name_from', {
              name: body.contents.nickName,
            }),
            title: '',
            description: this.$t('alarm.invite_refuse'),
            date: new Date(),
          })
          // if (!this.onPush) return
          this.alarmInviteDenied(body.contents.nickName)
          break
        case EVENT.LICENSE_EXPIRATION:
          this.alarmLicenseExpiration(body.contents.leftLicenseTime)
          break
        case EVENT.LICENSE_EXPIRED:
          if (this.$route.name === 'workspace') {
            this.clearWorkspace(this.workspace.uuid)
            this.alarmLicenseHome()
          } else {
            this.alarmLicense()
            setTimeout(() => {
              this.clearWorkspace(this.workspace.uuid)
              if (!this.$route.name !== 'workspace') {
                this.$call.leave()
                this.$router.push({ name: 'workspace' })
              }
            }, 60000)
          }
          break
        case EVENT.FORCE_LOGOUT:
          //contents에 담긴 sessionId와 같지 않은 경우 로그아웃 대상이다
          if (body.contents.sessionId !== this.statusSessionId) {
            auth.logout(false) //바로 로그아웃 처리
            //팝업 표시 후 리디렉트 실행
            this.confirmDefault(
              this.$t('workspace.confirm_duplicated_session_logout_received'),
              {
                action: () =>
                  (location.href = `${URLS['console']}/?continue=${location.href}`),
              },
            )
          }

          break
        default:
          return
      }
      this.active = true
    },
    remove(alarm) {
      this.removeAlarm(alarm.id)
    },
    // 초대 거절
    async inviteDenied(sessionId, userId) {
      const contents = {
        nickName: this.account.nickname,
      }

      sendPush(EVENT.INVITE_DENIED, [userId], contents)

      // 알람 리스트 업데이트
      this.inviteResponseAlarm({
        sessionId: sessionId,
        accept: 'refuse',
      })
      this.clearAlarm()
    },
    // 초대 수락
    async acceptInvite(sessionId, userId) {
      if (this.$call.session !== null) {
        this.toastError(this.$t('alarm.notice_already_call'))
        return
      }
      const params = {
        workspaceId: this.workspace.uuid,
        sessionId: sessionId,
      }
      try {
        const room = await getRoomInfo(params)
        const user = room.memberList.find(
          member => member.memberType === ROLE.LEADER,
        )
        const res = await this.join({
          ...room,
          leaderId: user ? user.uuid : null,
        })
        if (res !== true) return
        const contents = {
          nickName: this.account.nickname,
        }
        sendPush(EVENT.INVITE_ACCEPTED, [userId], contents)

        // 알람 리스트 업데이트
        this.inviteResponseAlarm({
          sessionId: sessionId,
          accept: 'accept',
        })
        this.clearAlarm()
      } catch (err) {
        if (err.code === 4002) {
          this.toastError(this.$t('workspace.remote_already_removed'))
          return
        } else {
          this.toastError(this.$t('workspace.remote_invite_impossible'))
        }
      }
    },
    async pushInit() {
      if (!this.hasLicense) return
      // const push = localStorage.getItem('push')
      this.key = this.$route.name
      // if (push === 'true') {
      //   this.onPush = true
      // } else if (push === 'false') {
      //   this.onPush = false
      // }
      if (!this.workspace.uuid) return
      await this.$push.init(this.workspace)
      this.$push.addListener(this.key, this.alarmListener)
    },
  },

  /* Lifecycles */
  mounted() {
    if (this.isSafari) {
      this.$refs['noticeAudio'].onloadeddata = this.loadeddata
      this.$refs['noticeAudio'].autoplay = true
    }
    this.$nextTick(() => {
      this.pushInit()
      let push = true
      if (localStorage.getItem('push')) {
        push = localStorage.getItem('push') === 'true'
      }
      this.onPush = push
    })
  },
  beforeDestroy() {
    this.$push.removeListener(this.key)
    if (this.isSafari) {
      window.removeEventListener('touchstart', this.loadAudio)
    }
  },
}
</script>

<style lang="scss">
@import '~assets/style/vars';

.popover.popover-notice {
  overflow: hidden;
  background-color: $color_bg_sub;
  border: solid 1px rgba($color_sub_border, 0.12);
  border-radius: 4px;
  transform: translateY(1.429rem);
  > .popover--body {
    padding: 0;
  }
}
.popover-notice__header {
  position: relative;
  display: flex;
  padding: 0.786rem 1.143rem;
  border-bottom: solid 1px rgba($color_line_border, 0.06);
  > span {
    // padding: 11px 12px;
    color: $color_text;
    font-size: 1.143rem;
  }
  & > .switcher {
    position: absolute;
    top: 50%;
    right: 1.357rem;
    transform: translateY(-50%);
  }
}
.popover-notice__body {
  height: 28.571rem;
  padding-right: 0.714rem;
  // border-bottom: solid 1px rgba($color_white, 0.09);
  > .vue-scrollbar__wrapper.popover-notice__scroller {
    height: 28.571rem;
  }
}
.popover-notice__empty {
  display: flex;
  width: 100%;
  height: 100%;
}
.popover-notice__empty-box {
  display: flex;
  flex-direction: column;
  width: fit-content;
  margin: auto;
  > img {
    width: fit-content;
    width: 6.857em;
    height: 6.857em;
    margin: auto;
  }
  > span {
    color: #d2d2d2;
    font-weight: 500;
    font-size: 1.143em;
  }
}
.popover-notice__footer {
  padding: 0.786rem 1rem;
  background-color: $color_darkgray_500;
  > span {
    color: rgba($color_text, 0.74);
    font-size: 0.929rem;
  }
}
</style>
