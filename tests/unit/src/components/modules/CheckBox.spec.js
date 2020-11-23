import { shallowMount, mount } from '@vue/test-utils'
import CheckBox from 'components/modules/CheckBox'

import localVue from '__utils__/localVue'

describe('CheckBox.vue', () => {
  const checkBoxText = 'checkbox test'

  it('text 전달', () => {
    const wrapper = shallowMount(CheckBox, {
      localVue,
      propsData: { value: true, text: checkBoxText },
    })
    expect(wrapper.text()).toMatch(checkBoxText)
  })

  it('체크 박스 클릭시 value(true) 반환', async () => {
    const wrapper = mount(CheckBox, {
      localVue,
      propsData: { value: false, text: checkBoxText },
    })

    expect(wrapper.find('div').exists()).toBe(true)
    await wrapper.find('div').trigger('click')
    await wrapper.vm.$nextTick()
    expect(wrapper.emitted()['update:value']).toEqual([[true]])
  })
})
