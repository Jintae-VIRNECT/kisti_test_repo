import { proxyUrl } from 'utils/file'
const ALARM_DURATION = 3 * 1000
const ALARM_DURATION_BUTTON = 60 * 1000

const buildTemplate = payload => {
  let icon = payload.icon
  if (payload.type === 'info' || payload.type === 'license') {
    icon = require('assets/image/header/ic_system.svg')
  } else if (payload.type === 'fail') {
    icon = require('assets/image/header/ic_notice.svg')
  } else if (!icon || icon.length === 0 || icon === 'default') {
    icon = false
  }
  let fullTemplate = `
  <figure>
    <div class="toasted--thumb">
      <div class="toasted--image">`
  if (icon) {
    fullTemplate += `
        <img
        src="${icon}"
        class="${payload.type}"
        />`
  }
  fullTemplate += `
      </div>
    </div>
    <figcaption>`
  if (payload.info && payload.info.length > 0) {
    fullTemplate += `
      <p class="toasted__info ${payload.type}">${payload.info}</p>`
  }
  fullTemplate += `
      <p class="toasted__description">${payload.description}</p>`
  if (payload.options && 'action' in payload.options) {
    fullTemplate += `
      <div class="toasted__buttons"></div>`
  }
  if (payload.options && 'changed' in payload.options) {
    fullTemplate += `
      <div class="toasted__buttons">
        <button class="${payload.options.changed.class}">${payload.options.changed.text}</button>
      </div>`
  }
  fullTemplate += `
    </figcaption>
  </figure>`

  return fullTemplate
}

export default {
  methods: {
    /**
     * 제거예정
     * 라이선스 만료 안내
     * @param {String} time
     */
    alarmLicenseExpiration(time) {
      this.alarmInfo(
        this.$t('alarm.expire_title'),
        this.$t('alarm.expire_time', { time }),
      )
    },
    /**
     * 참가자 협업 초대
     * @param {String, String} userInfo { nickName, profile }
     * @param {function} accept
     */
    alarmInvite({ nickName, profile }, accept, deny) {
      const refuse = () => {
        inviteNotify.text(
          buildTemplate({
            type: 'invite',
            info: this.$t('alarm.member_name', { name: nickName }),
            description: this.$t('alarm.invite_request'),
            icon: proxyUrl(profile),
            options: {
              changed: {
                text: this.$t('alarm.invite_refuse'),
                class: 'btn small disabled',
              },
            },
          }),
        )
        inviteNotify.goAway(ALARM_DURATION)
      }
      const inviteNotify = this.callNotify({
        type: 'invite',
        info: this.$t('alarm.member_name', { name: nickName }),
        description: this.$t('alarm.invite_request'),
        icon: proxyUrl(profile),
        duration: ALARM_DURATION_BUTTON,
        options: {
          action: [
            {
              text: this.$t('button.accept'),
              class: 'btn small',
              onClick: () => {
                inviteNotify.goAway()
                accept()
              },
            },
            {
              text: this.$t('button.refuse'),
              class: 'btn small sub',
              onClick: () => {
                refuse()
                deny()
              },
            },
            {
              text: '',
              class: 'btn small close',
              onClick: () => {
                inviteNotify.goAway()
              },
            },
          ],
        },
      })
    },
    /**
     * 참가자 협업 초대 거절
     * @param {String} nickName
     */
    alarmInviteDenied(nickName) {
      this.alarmInfo(
        '',
        this.$t('alarm.invite_refuse_name', { name: nickName }),
      )
    },
    /**
     * 참가자 협업 초대 수락
     * @param {String} nickName
     */
    alarmInviteAccepted(nickName) {
      this.alarmInfo(
        '',
        this.$t('alarm.invite_accept_name', { name: nickName }),
      )
    },
    /**
     * 제거예정
     * 라이선스 만료 메시지 (협업 진행 중)
     */
    alarmLicense() {
      this.callNotify({
        type: 'license',
        info: this.$t('alarm.expire_title'),
        description: this.$t('alarm.expire_logout'),
        duration: ALARM_DURATION_BUTTON,
      })
    },
    /**
     * 제거예정
     * 라이선스 만료 메시지 (홈화면)
     */
    alarmLicenseHome() {
      this.callNotify({
        type: 'license',
        info: this.$t('alarm.expire_title'),
        description: this.$t('alarm.expire_logout_home'),
        duration: ALARM_DURATION_BUTTON,
      })
    },
    /**
     * 미사용
     * TODO
     */
    alarmMessage(nickName, text) {
      this.callNotify({
        type: 'message',
        info: this.$t('alarm.member_name', { name: nickName }),
        description: text,
        icon: require('assets/image/profile.png'),
      })
    },
    /**
     * 미사용
     * TODO
     */
    alarmFail() {
      this.callNotify({
        type: 'fail',
        info: this.$t('alarm.invite_fail_title'),
        description: this.$t('alarm.invite_fail_maxuser'),
      })
    },
    /**
     * 미사용
     * TODO
     */
    alarmFile(nickName) {
      this.callNotify({
        type: 'file',
        info: this.$t('alarm.member_name', { name: nickName }),
        description: this.$t('alarm.file_link'),
        icon: require('assets/image/profile.png'),
        filelink: 'https://virnect.com',
        filename: 'VIRNECT Remote WEB 2.0.PDF',
        options: {
          action: {
            text: 'VIRNECT Remote WEB 2.0.PDF',
            class: 'btn small sub filelink',
            onClick: e => {
              window.open('https://virnect.com')
            },
          },
        },
      })
    },
    /**
     * 시스템 알림 메시지
     * @param {String} title
     * @param {String} description
     */
    alarmInfo(title, description) {
      const params = {
        type: 'info',
        info: title,
        description: description,
      }
      this.callNotify(params)
    },
    /**
     * 알림 토스트
     * @param type ['message', 'invite', 'info', 'fail', 'license', 'file']
     */
    callNotify(payload) {
      return this.$alarm.show(buildTemplate(payload), {
        position: 'top-right',
        duration: payload.duration | ALARM_DURATION,
        fitToScreen: true,
        keepOnHover: true,
        type: 'notify',
        ...payload.options,
      })
    },
    clearAlarm() {
      this.$alarm.clear()
    },
  },
}
