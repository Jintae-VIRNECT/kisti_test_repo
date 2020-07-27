<template>
  <popover
    trigger="click"
    placement="bottom-end"
    popperClass="popover-notice"
    width="29.571rem"
  >
    <toggle-button
      customClass="header-tools__notice toggle-header"
      slot="reference"
      description="알림"
      size="2.429rem"
      :toggle="false"
      :active="false"
      :activeSrc="require('assets/image/call/gnb_ic_notifi_nor.svg')"
      @action="notice"
      @click.native.stop="clickNotice"
    ></toggle-button>

    <div>
      <div class="popover-notice__header">
        <span>알림</span>
        <switcher text="Push" :value.sync="onPush">Push</switcher>
      </div>
      <div class="popover-notice__body">
        <scroller height="28.571rem" v-if="alarmList.length > 0">
          <notice-item
            type="file"
            :info="'Harry Ha 님'"
            :description="'파일 링크 전달드립니다.'"
            :date="'2020.01.20 오전 11:00'"
            :filename="'VIRNECT Remote WEB 2.0'"
            :filelink="'https://virnect.com'"
            :image="'default'"
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
            <span>수신받은 알람이 없습니다.</span>
          </div>
        </div>
      </div>
      <div class="popover-notice__footer">
        <span>알림은 30일 동안 보관됩니다.</span>
      </div>
    </div>
    <audio preload="auto" ref="noticeAudio">
      <source src="~assets/media/end.mp3" />
    </audio>
  </popover>
</template>

<script>
import { mapActions } from 'vuex'
import { EVENT } from 'configs/push.config'
import { sendPush } from 'api/common/message'
import { getRoomInfo } from 'api/workspace'

import Switcher from 'Switcher'
import Popover from 'Popover'
import ToggleButton from 'ToggleButton'
import Scroller from 'Scroller'
import NoticeItem from './NoticeItem'

import alarmMixin from 'mixins/alarm'
import roomMixin from 'mixins/room'
import toastMixin from 'mixins/toast'

export default {
  name: 'Notice',
  mixins: [roomMixin, alarmMixin, toastMixin],
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
      alarmList: [],
    }
  },
  watch: {
    onPush(push) {
      if (push) {
        this.$localStorage.setItem('push', 'true')
      } else {
        this.$localStorage.setItem('push', 'false')
      }
    },
  },
  methods: {
    ...mapActions(['setRoomInfo', 'roomClear']),
    clickNotice() {
      this.checkBeta()
    },
    notice() {
      if (this.onPush) return
      console.log('notice list refresh logic')
    },
    async alarmListener(listen) {
      // if (!this.onPush) return
      const body = JSON.parse(listen.body)

      if (body.targetUserIds.indexOf(this.account.uuid) < 0) return
      if (body.userId === this.account.uuid) return

      this.logger('received message::', body)

      switch (body.event) {
        case EVENT.INVITE:
          this.$refs['noticeAudio'].play()
          this.alarmInvite(
            body.contents,
            () => this.acceptInvite(body),
            () => this.inviteDenied(body.userId),
          )
          break
        case EVENT.INVITE_ACCEPTED:
          this.alarmInviteAccepted(body.contents.nickName)
          break
        case EVENT.INVITE_DENIED:
          this.alarmInviteDenied(body.contents.nickName)
          break
        case EVENT.LICENSE_EXPIRATION:
          this.alarmLicenseExpiration(body.contents.leftLicenseTime)
          break
        case EVENT.LICENSE_EXPIRED:
          this.alarmLicense()
          setTimeout(() => {
            this.$call.leave()
            this.$router.push({ name: 'workspace' })
          }, 60000)
          break
      }
    },
    async inviteDenied(userId) {
      const contents = {
        nickName: this.account.nickname,
      }

      await sendPush(EVENT.INVITE_DENIED, [userId], contents)
    },
    async acceptInvite(body) {
      if (this.$call.session !== null) {
        // TODO: MESSAGE
        alert('통화를 종료하고 참여해주세요')
        return
      }
      const params = {
        workspaceId: this.workspace.uuid,
        sessionId: body.contents.roomSessionId,
      }
      try {
        const room = await getRoomInfo(params)
        this.join(room)
        const contents = {
          nickName: this.account.nickname,
        }
        sendPush(EVENT.INVITE_ACCEPTED, [body.userId], contents)
      } catch (err) {
        if (err.code === 4002) {
          this.toastError('이미 삭제된 협업입니다.')
          // this.toastError('협업에 참가가 불가능합니다.')
        }
      }
    },
  },

  /* Lifecycles */
  mounted() {
    const push = this.$localStorage.getItem('push')
    this.key = this.$route.name
    if (push === 'true') {
      this.onPush = true
    } else if (push === 'false') {
      this.onPush = false
    }
    this.$nextTick(async () => {
      await this.$push.init(this.workspace.uuid)
      this.$push.addListener(this.key, this.alarmListener)
    })
  },
  beforeDestroy() {
    this.$push.removeListener(this.key)
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
  border-bottom: solid 1px rgba($color_white, 0.09);
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
    margin: auto;
  }
  > span {
    color: #d2d2d2;
    font-weight: 500;
    font-size: 16px;
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
