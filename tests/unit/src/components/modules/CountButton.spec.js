import { shallowMount } from '@vue/test-utils'
import CountButton from 'components/modules/CountButton'
import localVue from '__utils__/localVue'

describe('CountButton.vue', () => {
  const images = {
    select: 'assets/image/ic_rec_select.svg',
    active: 'assets/image/ic_rec_active.svg',
    default: 'assets/image/ic_rec_default.svg',
  }
  it('count 표시', () => {
    const count = 100
    const wrapper = shallowMount(CountButton, {
      localVue,
      propsData: {
        count: count,
        images: { images },
      },
    })
    expect(wrapper.props('count')).toBe(count)
  })

  it('count 가 0일때 nodata 스타일 클래스 표시', () => {
    const count = 0
    const wrapper = shallowMount(CountButton, {
      localVue,
      propsData: {
        count: count,
        images: { images },
      },
    })
    expect(wrapper.find('.nodata').exists(true)).toEqual(true)
  })
})
