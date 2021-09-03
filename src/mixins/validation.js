/**
 * 벨리데이션 mixin
 * el-form 에서 사용하는 rules
 */
import { extend } from 'vee-validate'
import { required } from 'vee-validate/dist/rules'
export default {
  data() {
    return {
      rules: {
        id: [
          {
            required: true,
            message: this.$t('invalid.required', [
              this.$t('members.create.id'),
            ]),
          },
          {
            validator: (rule, value, callback) => {
              if (!/^.{4,20}$/.test(value)) {
                callback(
                  new Error(this.$t('members.create.caution.validUserId')),
                )
              } else if (!/^[a-z][a-z0-9]*$/i.test(value)) {
                callback(
                  new Error(this.$t('members.create.caution.validUserId')),
                )
              } else {
                callback()
              }
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
                callback(new Error(this.$t('members.setting.password.caution')))
              }
              if (!/^.{8,20}$/.test(value)) {
                callback(new Error(this.$t('members.setting.password.caution')))
              }
              if (/(.)\1\1\1/.test(value)) {
                callback(new Error(this.$t('members.setting.password.caution')))
              }
              if (/(0123|1234|2345|3456|4567|5678|6789|7890)/.test(value)) {
                callback(new Error(this.$t('members.setting.password.caution')))
              }
              if (/(0987|9876|8765|7654|6543|5432|4321|3210)/.test(value)) {
                callback(new Error(this.$t('members.setting.password.caution')))
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
      nicknameCheck(nickname) {
        if (nickname.trim().length < 1 || nickname.trim().length > 20) {
          return false
        } else if (/[<>]/.test(nickname)) {
          return false
        }
        return true
      },
      passwordCheck(password) {
        let typeCount = 0
        if (/[0-9]/.test(password)) typeCount++
        if (/[a-z]/.test(password)) typeCount++
        if (/[A-Z]/.test(password)) typeCount++
        if (/[$.$,$!$@$#$$$%]/.test(password)) typeCount++

        if (typeCount < 3) return false
        if (!/^.{8,20}$/.test(password)) return false
        if (/(.)\1\1\1/.test(password)) return false
        if (/(0123|1234|2345|3456|4567|5678|6789|7890)/.test(password))
          return false
        if (/(0987|9876|8765|7654|6543|5432|4321|3210)/.test(password))
          return false
        return true
      },
      deleteDescCheck(desc) {
        if ('Delete Account' !== desc) {
          return false
        }
        return true
      },
    }
  },
  created() {
    // vee-validate에서 required 제공

    // vee-validate 를 이용한 처리
    extend('requiredPassword', {
      ...required,
      message: this.$t('members.setting.password.caution'),
    })
    extend('passwordCheck', {
      validate: this.passwordCheck,
      message: this.$t('members.setting.password.caution'),
    })

    extend('requiredNickname', {
      ...required,
      message: this.$t('members.setting.nickname.caution'),
    })
    extend('nicknameCheck', {
      validate: this.nicknameCheck,
      message: this.$t('members.setting.nickname.caution'),
    })

    extend('requiredDeleteDesc', {
      ...required,
      message: this.$t('members.setting.delete.desc'),
    })
    extend('deleteDescCheck', {
      validate: this.deleteDescCheck,
      message: this.$t('members.setting.delete.desc'),
    })
  },
}
