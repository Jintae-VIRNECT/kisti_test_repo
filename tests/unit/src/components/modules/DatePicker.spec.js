import { shallowMount } from '@vue/test-utils'
import DatePicker from 'components/modules/Datepicker'

import localVue from '__utils__/localVue'

//call from mocked store
import store from 'store'
import i18n from 'plugins/remote/i18n'

describe('DatePicker.vue', () => {
  //mock calendar
  store.getters.calendars = [
    {
      name: 'test1',
      date: new Date(),
      status: true,
    },
    {
      name: 'test2',
      date: new Date(),
      status: false,
    },
  ]

  it('초기 로딩 이후 computed의 calendar는 store내의 calendar를 비교하여 pickerName과 동일한 캘린더 객체를 반환', () => {
    const wrapper = shallowMount(DatePicker, {
      localVue,
      i18n,
      store,
      propsData: {
        pickerName: 'test1',
      },
    })
    expect(wrapper.vm.calendar.name).toEqual('test1')
  })

  it('활성 상태일 때 calendar button이 active 이미지 출력', async () => {
    const wrapper = shallowMount(DatePicker, {
      localVue,
      i18n,
      store,
      propsData: {
        pickerName: 'test1',
      },
    })
    await wrapper.setData({ isActive: true })

    expect(wrapper.find('.calendar-button').find('img').element.src).toContain(
      'ic_calendar_active',
    )
  })
})
