import { shallowMount } from '@vue/test-utils'

import Collapsible from 'components/modules/Collapsible'

import localVue from '__utils__/localVue'

describe('Collapsible.vue', () => {
  it('mount', () => {
    const wrapper = shallowMount(Collapsible, {
      localVue,
    })

    expect(wrapper.isVisible()).toBe(true)
  })

  it('button 클릭시 toggle', async () => {
    const wrapper = shallowMount(Collapsible, {
      localVue,
      propsData: { count: 10 },
    })
    await wrapper.find('button').trigger('click')

    expect(wrapper.vm.$data.open).toBe(true)
  })

  it('count가 0보다 클때 toggle 메서드 호출시 open 되는지 확인', () => {
    const wrapper = shallowMount(Collapsible, {
      localVue,
      propsData: { count: 10 },
    })

    expect(wrapper.vm.$data.open).toBe(false)

    wrapper.vm.toggle()

    expect(wrapper.vm.$data.open).toBe(true)
  })

  it('count가 0일때 toggle 메서드 호출시 open 되면 안됨.', () => {
    const wrapper = shallowMount(Collapsible, {
      localVue,
      propsData: { count: 0 },
    })

    expect(wrapper.vm.$data.open).toBe(false)

    wrapper.vm.toggle()

    expect(wrapper.vm.$data.open).toBe(false)
  })

  it('mount시 count가 0크고 preOpen이 true일 때 open 상태로 변경.', () => {
    const wrapper = shallowMount(Collapsible, {
      localVue,
      propsData: { count: 10, preOpen: true },
    })

    expect(wrapper.vm.$data.open).toBe(true)
  })

  it('count가 preOpen이 true일 때 open 상태로 변경.', () => {
    const wrapper = shallowMount(Collapsible, {
      localVue,
      propsData: { count: 10, preOpen: true },
    })

    expect(wrapper.vm.$data.open).toBe(true)
  })

  it('count가 양수에서 0 이하값으로 변경시 close', async () => {
    const wrapper = shallowMount(Collapsible, {
      localVue,
      propsData: { count: 10, preOpen: true },
    })

    expect(wrapper.vm.$data.open).toBe(true)

    await wrapper.setProps({ count: 0 })

    expect(wrapper.vm.$data.open).toBe(false)
  })
})
