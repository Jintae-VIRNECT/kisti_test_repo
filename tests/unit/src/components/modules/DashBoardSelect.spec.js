import { shallowMount, mount } from '@vue/test-utils'
import DashBoardSelect from 'components/modules/DashBoardSelect'
import localVue from '__utils__/localVue'

describe('DashBoardSelect.vue', () => {
  const options = [
    {
      value: 'ALL',
      text: '전체',
    },
    {
      value: 'ONGOING',
      text: '진행중',
    },
    {
      value: 'END',
      text: '종료',
    },
  ]
  it('selectedValue가 지정되지 않았을때 options의 첫번째 값 기본 설정', async () => {
    const wrapper = mount(DashBoardSelect, {
      localVue,
      propsData: {
        options: options,
      },
    })

    await wrapper.vm.$nextTick()
    expect(wrapper.emitted()['update:selectedValue']).toEqual([
      [options[0]['value']],
    ])
  })

  it('메뉴 선택시 option 선택 버튼 출력 및 두번째 항목 선택', async () => {
    const wrapper = shallowMount(DashBoardSelect, {
      localVue,
      propsData: {
        options: options,
      },
    })

    await wrapper.vm.$nextTick()
    const selectLabel = wrapper.find('.select-label')
    await selectLabel.trigger('click')

    await wrapper.findAll('.select-option').at(1).trigger('click')
    await wrapper.vm.$nextTick()

    expect(wrapper.emitted()['update:selectedValue'][1]).toEqual([
      options[1]['value'],
    ])
  })

  it('selectedValue를 props로 전달시 options에서 해당값 찾아 미리 선택', async () => {
    const wrapper = shallowMount(DashBoardSelect, {
      localVue,
      propsData: {
        options: options,
        selectedValue: options[2].value,
      },
    })
    await wrapper.vm.$nextTick()

    expect(wrapper.emitted()['update:selectedValue']).toEqual([
      [options[2].value],
    ])
  })
})
