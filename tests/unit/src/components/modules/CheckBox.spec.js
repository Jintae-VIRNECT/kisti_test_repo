import { shallowMount } from '@vue/test-utils'
import Card from 'components/modules/CheckBox'

describe('CheckBox.vue', () => {
  it('커스텀 클래스 전달', () => {
    const wrapper = shallowMount(Card, {
      propsData: { value: true, text: 'checkbox test' },
    })
    expect(wrapper.text()).toMatch('checkbox test')
  })
})
