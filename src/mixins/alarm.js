export default {
  methods: {
    /**
     * 알림 토스트
     * @param type ['message', 'invite', 'info', 'fail', 'license', 'file']
     */
    alarmMessage() {
      this.callNotify({
        type: 'message',
        info: 'Nari Han 님',
        description:
          '일이삼사오육칠팔구십일이삼사오육칠팔구십일이삼사오육칠팔구십일이삼사오육칠팔구십',
        icon: require('assets/image/profile.png'),
      })
    },
    alarmInvite() {
      this.callNotify({
        type: 'invite',
        info: 'Nari Han 님',
        description: '참가자로 협업을 요청하였습니다.',
        icon: require('assets/image/profile.png'),
        options: {
          action: [
            {
              text: '수락',
              class: 'btn small',
              onClick: e => {
                console.log('수락')
              },
            },
            {
              text: '거절',
              class: 'btn small sub',
              onClick: e => {
                console.log('거절')
              },
            },
          ],
        },
      })
    },
    alarmInfo() {
      this.callNotify({
        type: 'info',
        info: '[만료안내]',
        description: '라이선스 만료 <em>[60분]</em> 남았습니다.',
      })
    },
    alarmFail() {
      this.callNotify({
        type: 'fail',
        info: '[협업 참가 실패]',
        description: '최대 참가인원이 초과하였습니다.',
      })
    },
    alarmLicense() {
      this.callNotify({
        type: 'license',
        info: '[만료안내]',
        description: '라이선스가 만료되었습니다.',
        options: {
          action: {
            text: '라이선스 구매하기',
            class: 'btn small',
            onClick: (e, toastObject) => {
              toastObject.goAway(0)
            },
          },
        },
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
    callNotify(payload) {
      let icon = payload.icon
      if (payload.type === 'info' || payload.type === 'license') {
        icon = require('assets/image/ic_system.svg')
      } else if (payload.type === 'fail') {
        icon = require('assets/image/ic_notice.svg')
      }
      let template = ``
      if (payload.options && 'action' in payload.options) {
        template = `<div class="toasted__buttons"></div>`
      }
      this.$alarm.show(
        `
        <figure>
          <div class="toasted__thumb ${payload.type}">
            <img src="${icon}" />
          </div>
          <figcaption>
            <p class="toasted__info ${payload.type}">${payload.info}</p>
            <p class="toasted__description">${payload.description}</p>
        ` +
          template +
          `
          </figcaption>
        </figure>`,
        {
          position: 'top-right',
          duration: 50000,
          fitToScreen: true,
          keepOnHover: true,
          type: 'notify',
          ...payload.options,
        },
      )
    },
  },
}
