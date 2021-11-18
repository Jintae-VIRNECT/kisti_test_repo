/**
 * @jest-environment jsdom
 */
import { shallowMount } from '@vue/test-utils'
import mocks from '__mocks__'
import Login from './Login.vue'
import Cookies from 'js-cookie'

const login = {
  email: '',
  password: '',
  rememberMe: null,
  autoLogin: null,
}

describe('Login test', propsData => {
  let cmp
  beforeEach(() => {
    cmp = shallowMount(Login, {
      propsData: {
        ...propsData,
      },
      ...mocks,
    })
  })

  it('show status check', () => {
    expect(cmp.vm.login).toStrictEqual(login)
  })
  it('token check', () => {
    expect(cmp.vm.token).toBe(Cookies.get('accessToken'))
  })
})
