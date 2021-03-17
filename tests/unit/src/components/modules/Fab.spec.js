import { shallowMount } from '@vue/test-utils'
import Fab from 'components/modules/Fab'
import localVue from '__utils__/localVue'

describe('Fab.vue', () => {
  it('버튼 렌더링 확인', async () => {
    const wrapper = shallowMount(Fab, {
      localVue,
    })

    expect(wrapper.find('button').exists()).toBe(true)
  })
})
