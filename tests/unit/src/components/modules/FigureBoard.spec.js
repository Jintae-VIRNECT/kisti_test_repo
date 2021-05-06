import { shallowMount } from '@vue/test-utils'
import FigureBoard from 'components/modules/FigureBoard'
import localVue from '__utils__/localVue'

describe('FigureBoard.vue', () => {
  it('props로 전달된 데이터 렌더링', async () => {
    const figureHeader = '피겨헤더'
    const wrapper = shallowMount(FigureBoard, {
      localVue,
      propsData: {
        header: figureHeader,
        my: true,
        count: 123,
        imgSrc: '',
        type: 'monthly',
      },
    })
    const wrapperText = wrapper.text()
    expect(wrapperText.includes(figureHeader)).toEqual(true)
    expect(wrapperText.includes(123)).toEqual(true)
  })

  it('time은 시단위, 분단위로 표현됨', async () => {
    const figureHeader = '피겨헤더'
    const wrapper = shallowMount(FigureBoard, {
      localVue,
      propsData: {
        header: figureHeader,
        my: true,
        time: 123456,
        imgSrc: '',
        type: 'monthly',
      },
    })
    expect(wrapper.vm.hour).toEqual(34)
    expect(wrapper.vm.min).toEqual(17)
  })

  it('10시간 미만은 앞에 0 표기', () => {
    const figureHeader = '피겨헤더'
    const wrapper = shallowMount(FigureBoard, {
      localVue,
      propsData: {
        header: figureHeader,
        my: true,
        time: 3599,
        imgSrc: '',
        type: 'monthly',
      },
    })
    expect(wrapper.vm.hour).toEqual('00')
    expect(wrapper.vm.min).toEqual(59)
  })

  it('분 표기시 10분 미만은 0 표시', () => {
    const figureHeader = '피겨헤더'
    const wrapper = shallowMount(FigureBoard, {
      localVue,
      propsData: {
        header: figureHeader,
        my: true,
        time: 61,
        imgSrc: '',
        type: 'monthly',
      },
    })
    expect(wrapper.vm.hour).toEqual('00')
    expect(wrapper.vm.min).toEqual('01')
  })
})
