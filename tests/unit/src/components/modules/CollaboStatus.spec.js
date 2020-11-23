import { shallowMount, mount } from '@vue/test-utils'
import CollaboStatus from 'components/modules/CollaboStatus'

import localVue from '__utils__/localVue'

describe('CollaboStatus', () => {
  it('커스텀 스타일 전달', () => {
    const wrapper = shallowMount(CollaboStatus, {
      localVue,
      propsData: { customClass: 'customClass' },
    })

    expect(wrapper.find('.customClass').exists(true)).toEqual(true)
  })
})
