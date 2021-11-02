import Vue from 'vue'
import { ValidationProvider, ValidationObserver, extend } from 'vee-validate'
import * as defaultRules from 'vee-validate/dist/rules'
import { required } from 'vee-validate/dist/rules'

// 에러메세지 커스텀도 여기서 가능
for (const rule in defaultRules) {
  extend(rule, { ...defaultRules[rule] }) // eslint-disable-line
}
function idCheck(id) {
  if (!/^[A-Za-z]{1}[A-Za-z0-9]{3,20}$/i.test(id)) {
    return false
  }
  return true
}
function nicknameCheck(nickname) {
  if (nickname.trim().length < 1 || nickname.trim().length > 20) {
    return false
  } else if (/[<>]/.test(nickname)) {
    return false
  }
  return true
}
function passwordCheck(password) {
  let typeCount = 0
  if (/[0-9]/.test(password)) typeCount++
  if (/[a-z]/.test(password)) typeCount++
  if (/[A-Z]/.test(password)) typeCount++
  if (/[$.$,$!$@$#$$$%]/.test(password)) typeCount++

  if (typeCount < 3) return false
  if (!/^.{8,20}$/.test(password)) return false
  if (/(.)\1\1\1/.test(password)) return false
  if (/(0123|1234|2345|3456|4567|5678|6789|7890)/.test(password)) return false
  if (/(0987|9876|8765|7654|6543|5432|4321|3210)/.test(password)) return false
  return true
}
function deleteDescCheck(desc) {
  if ('Delete Account' !== desc) {
    return false
  }
  return true
}
function ipAddressCheck(ip) {
  if (
    !/(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}/.test(
      ip,
    )
  )
    return false
  return true
}

extend('requiredPassword', {
  ...required,
  message: 'members.setting.password.caution',
})
extend('passwordCheck', {
  validate: passwordCheck,
  message: 'members.setting.password.caution',
})
extend('requiredNickname', {
  ...required,
  message: 'members.setting.nickname.caution',
})
extend('idCheck', {
  validate: idCheck,
  message: 'members.create.caution.validUserId',
})
extend('nicknameCheck', {
  validate: nicknameCheck,
  message: 'members.setting.nickname.caution',
})
extend('requiredDeleteDesc', {
  ...required,
  message: 'members.setting.delete.desc',
})
extend('deleteDescCheck', {
  validate: deleteDescCheck,
  message: 'members.setting.delete.desc',
})
extend('ipAddressCheck', {
  validate: ipAddressCheck,
  message: 'workspace.onpremiseSetting.ip.list.warning',
})

Vue.component('ValidationProvider', ValidationProvider)
Vue.component('ValidationObserver', ValidationObserver)
