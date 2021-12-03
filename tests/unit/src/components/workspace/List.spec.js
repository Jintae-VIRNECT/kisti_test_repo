import { shallowMount } from '@vue/test-utils'
import beforeAllTests from '~/tests/beforeAllTests'
import localVue from '__utils__/localVue'

import List from 'components/workspace/List'

describe('WorkspaceList.vue', () => {
  beforeAll(async () => {
    beforeAllTests()
  })
  it('mount test', async () => {
    const isHome = true
    const wrapper = shallowMount(List, {
      localVue,
      propsData: { isHome },
    })
    await wrapper.vm.$nextTick()
    console.log(wrapper.html())
    expect(wrapper.isVisible()).toBe(true)
    expect(wrapper.vm.$props.isHome).toBe(true)
  })
})
