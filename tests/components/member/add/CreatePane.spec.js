import { shallowMount } from '@vue/test-utils'
import CreatePane from '@/components/member/add/CreatePane.vue'

import localVue from '__utils__/localVue'
import store from '__mocks__/store'

describe('CreatePane.vue', () => {
  it('CreatePane.vue mounted', () => {
    const wrapper = shallowMount(CreatePane, {
      store,
      localVue,
      stubs: {
        MemberAddUsage: true,
        MemberPlanSelect: true,
        MemberRoleSelect: true,
        ValidationProvider: true,
      },
    })
    expect(wrapper.isVisible()).toBe(true)
  })
})
