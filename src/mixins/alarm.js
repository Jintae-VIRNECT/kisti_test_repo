export default {
  methods: {
    /**
     * 라이선스 만료 안내
     * @param {String} time
     */
    alarmLicenseExpiration(time) {
      this.alarmInfo(
        '[만료안내]',
        `라이선스 만료 <em>[${time}분]</em> 남았습니다.`,
      )
    },
    /**
     * 참가자 협업 초대
     * @param {String, String} userInfo { nickName, profile }
     * @param {function} accept
     */
    alarmInvite({ nickName, profile }, accept) {
      const refuse = () => {
        inviteNotify.text(
          this.buildTemplate({
            type: 'invite',
            info: `${nickName} 님`,
            description: '참가자로 협업을 요청하였습니다.',
            icon: profile,
            options: {
              changed: {
                text: '협업 요청을 거절하였습니다.',
                class: 'btn small disabled',
              },
            },
          }),
        )
        inviteNotify.goAway(3000)
      }
      const inviteNotify = this.callNotify({
        type: 'invite',
        info: `${nickName} 님`,
        description: '참가자로 협업을 요청하였습니다.',
        icon: profile,
        options: {
          action: [
            {
              text: '수락',
              class: 'btn small',
              onClick: () => {
                inviteNotify.goAway()
                accept()
              },
            },
            {
              text: '거절',
              class: 'btn small sub',
              onClick: refuse,
            },
          ],
        },
      })
      console.log(inviteNotify)
    },
    /**
     * 시스템 알림 메시지
     * @param {String} title
     * @param {String} description
     */
    alarmInfo(title, description) {
      this.callNotify({
        type: 'info',
        info: title,
        description: description,
      })
    },
    /**
     * 라이선스 만료 메시지
     */
    alarmLicense() {
      this.callNotify({
        type: 'license',
        info: '[만료안내]',
        description: '라이선스가 만료되었습니다.<br> 1분 뒤에 자동 종료됩니다.',
        // options: {
        //   action: {
        //     text: '라이선스 구매하기',
        //     class: 'btn small',
        //     onClick: (e, toastObject) => {
        //       toastObject.goAway(0)
        //     },
        //   },
        // },
      })
    },
    alarmMessage() {
      this.callNotify({
        type: 'message',
        info: 'Nari Han 님',
        description:
          '일이삼사오육칠팔구십일이삼사오육칠팔구십일이삼사오육칠팔구십일이삼사오육칠팔구십',
        icon: require('assets/image/profile.png'),
      })
    },
    alarmFail() {
      this.callNotify({
        type: 'fail',
        info: '[협업 참가 실패]',
        description: '최대 참가인원이 초과하였습니다.',
      })
    },
    alarmFile() {
      this.callNotify({
        type: 'file',
        info: 'Nari Han 님',
        description: '파일 링크 전달드립니다.',
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
    buildTemplate(payload) {
      let icon = payload.icon
      if (payload.type === 'info' || payload.type === 'license') {
        icon = require('assets/image/ic_system.svg')
      } else if (payload.type === 'fail') {
        icon = require('assets/image/ic_notice.svg')
      } else if (!icon || icon.length === 0 || icon === 'default') {
        icon = false
      }
      let iconTemplate = `
      <div class="toasted--thumb">
        <div class="toasted--image">
      `
      if (icon) {
        iconTemplate += `
        <img
        src="${icon}"
        class="${payload.type}"
        />
        `
      }
      iconTemplate += `
        </div>
      </div>
      `
      let btnTemplate = ``
      if (payload.options && 'action' in payload.options) {
        btnTemplate = `<div class="toasted__buttons"></div>`
      }
      if (payload.options && 'changed' in payload.options) {
        btnTemplate = `
        <div class="toasted__buttons">
          <button class="${payload.options.changed.class}">${payload.options.changed.text}</button>
        </div>`
      }

      const fullTemplate =
        `
      <figure>
      ${iconTemplate}
        <figcaption>
          <p class="toasted__info ${payload.type}">${payload.info}</p>
          <p class="toasted__description">${payload.description}</p>
      ` +
        btnTemplate +
        `
        </figcaption>
      </figure>`
      console.log(fullTemplate)

      return fullTemplate
    },
    /**
     * 알림 토스트
     * @param type ['message', 'invite', 'info', 'fail', 'license', 'file']
     */
    callNotify(payload) {
      return this.$alarm.show(this.buildTemplate(payload), {
        position: 'top-right',
        duration: 50000000,
        fitToScreen: true,
        keepOnHover: true,
        type: 'notify',
        ...payload.options,
      })
    },
  },
}
