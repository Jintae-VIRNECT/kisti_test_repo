/**
 * @jest-environment jsdom
 */
import { shallowMount } from '@vue/test-utils'
import mocks from '__mocks__'
import LoginCenter from './LoginCenter.vue'

describe('LoginCenter test', propsData => {
  let cmp
  beforeEach(() => {
    cmp = shallowMount(LoginCenter, {
      propsData: {
        ...propsData,
      },
      ...mocks,
    })
  })

  it('false 보여줘', () => {
    expect(cmp.vm.show).toBe(false)
  })
})
