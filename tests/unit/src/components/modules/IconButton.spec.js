import { mount } from '@vue/test-utils'
import IconButton from 'components/modules/IconButton'
import localVue from '__utils__/localVue'

describe('IconButton.vue', () => {
  const text = '테스트'
  it('text 렌더링 확인', () => {
    const wrapper = mount(IconButton, {
      localVue,
      propsData: {
        text: text,
        activeImgSrc: 'assets/image/ic_down_on.svg',
        selectImgSrc: 'assets/image/ic_down_on.svg',
        imgSrc: 'assets/image/ic_down_off.svg',
      },
    })
    console.log(wrapper.html())
    expect(wrapper.text().includes(text)).toEqual(true)
  })

  it('mousedown시 showActiveImg true', async () => {
    const wrapper = mount(IconButton, {
      localVue,
      propsData: {
        text: text,
        activeImgSrc: 'assets/image/ic_down_on.svg',
        selectImgSrc: 'assets/image/ic_down_on.svg',
        imgSrc: 'assets/image/ic_down_off.svg',
      },
    })
    await wrapper.find('button').trigger('mousedown')
    await wrapper.vm.$nextTick()
    expect(wrapper.vm.showActiveImg).toEqual(true)
  })

  it('mouseup시 showSelectImg false', async () => {
    const wrapper = mount(IconButton, {
      localVue,
      propsData: {
        text: text,
        activeImgSrc: 'assets/image/ic_down_on.svg',
        selectImgSrc: 'assets/image/ic_down_on.svg',
        imgSrc: 'assets/image/ic_down_off.svg',
      },
    })
    await wrapper.find('button').trigger('mouseup')
    await wrapper.vm.$nextTick()
    expect(wrapper.vm.showSelectImg).toEqual(false)
  })

  it('mouseover showSelectImg false', async () => {
    const wrapper = mount(IconButton, {
      localVue,
      propsData: {
        text: text,
        activeImgSrc: 'assets/image/ic_down_on.svg',
        selectImgSrc: 'assets/image/ic_down_on.svg',
        imgSrc: 'assets/image/ic_down_off.svg',
      },
    })
    await wrapper.find('button').trigger('mouseover')
    await wrapper.vm.$nextTick()
    expect(wrapper.vm.showSelectImg).toEqual(false)
  })
})
