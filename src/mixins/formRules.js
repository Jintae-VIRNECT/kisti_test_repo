/**
 * 벨리데이션 mixin
 * el-form 에서 사용하는 rules
 */
export default {
  data() {
    return {
      rules: {
        id: [
          {
            required: true,
            message: ' ',
          },
          {
            validator: (rule, value, callback) => {
              if (!/^[A-Za-z]{1}[A-Za-z0-9]{3,20}$/i.test(value)) {
                callback(new Error(' '))
              }
              callback()
            },
          },
        ],
        password: [
          {
            validator: (rule, value, callback) => {
              let typeCount = 0
              if (/[0-9]/.test(value)) typeCount++
              if (/[a-z]/.test(value)) typeCount++
              if (/[A-Z]/.test(value)) typeCount++
              if (/[$.$,$!$@$#$$$%]/.test(value)) typeCount++

              if (typeCount < 3) {
                callback(new Error(' '))
              }
              if (!/^.{8,20}$/.test(value)) {
                callback(new Error(' '))
              }
              if (/(.)\1\1\1/.test(value)) {
                callback(new Error(' '))
              }
              if (/(0123|1234|2345|3456|4567|5678|6789|7890)/.test(value)) {
                callback(new Error(' '))
              }
              if (/(0987|9876|8765|7654|6543|5432|4321|3210)/.test(value)) {
                callback(new Error(' '))
              }

              callback()
            },
          },
        ],
        email: [
          {
            required: true,
            message: this.$t('invalid.required', [
              this.$t('members.add.email'),
            ]),
          },
          {
            type: 'email',
            message: this.$t('invalid.format', [this.$t('members.add.email')]),
          },
        ],
      },
    }
  },
}
