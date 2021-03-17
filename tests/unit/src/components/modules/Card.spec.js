import { shallowMount } from '@vue/test-utils'
import Card from 'components/modules/Card'

describe('Card.vue', () => {
  it('커스텀 클래스 전달', () => {
    const customClass = 'customClass'
    const wrapper = shallowMount(Card, {
      propsData: { customClass },
    })
    expect(wrapper.classes()).toContain(customClass)
  })
})
