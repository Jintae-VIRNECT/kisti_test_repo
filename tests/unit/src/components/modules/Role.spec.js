import { shallowMount } from '@vue/test-utils'

import Role from 'components/modules/Role'

import localVue from '__utils__/localVue'

describe('기본 mount', () => {
  it('커스텀 클래스 전달', () => {
    const role = 'master'
    const wrapper = shallowMount(Role, {
      localVue,
      propsData: { role },
    })
    expect(wrapper.text()).toContain(role)
  })
})
