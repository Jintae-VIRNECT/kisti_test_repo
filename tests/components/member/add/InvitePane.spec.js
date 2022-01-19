import { shallowMount } from '@vue/test-utils'
import InvitePane from '@/components/member/add/InvitePane.vue'
import InviteMember from '@/models/workspace/InviteMember'
import localVue from '__utils__/localVue'
import store from '__mocks__/store'

describe('InvitePane.vue', () => {
  it('InvitePane.vue mounted', () => {
    const wrapper = shallowMount(InvitePane, {
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

describe('다국어 i18n key 값 확인', () => {
  let wrapper
  beforeEach(() => {
    wrapper = shallowMount(InvitePane, {
      store,
      localVue,
    })
  })
  it('members.invitation.title 확인', () => {
    const element = wrapper.find('.invite-pane__title h6')
    expect(element.exists()).toBe(true)
    expect(element.text()).toEqual('members.invitation.title')
  })

  it('members.invitation.desc 확인', () => {
    const element = wrapper.find('.invite-pane__title p')
    expect(element.exists()).toBe(true)
    expect(element.text()).toEqual('members.invitation.desc')
  })

  it('members.invitation.list 확인', () => {
    const element = wrapper.find('.invite-pane__sub-title p')
    expect(element.exists()).toBe(true)
    expect(element.text()).toEqual('members.invitation.list')
  })

  it('members.add.addUser 확인', () => {
    const element = wrapper.find('.invite-pane__content h6 span')
    expect(element.exists()).toBe(true)
    expect(element.text()).toEqual('members.add.addUser 1')
  })

  it('members.add.addMember 확인', () => {
    const element = wrapper.find(
      '.invite-pane__footer el-button-stub[type="default"]',
    )
    expect(element.exists()).toBe(true)
    expect(element.text()).toEqual('members.add.addMember')
  })

  it('members.add.submit 확인', () => {
    const element = wrapper.find(
      '.invite-pane__footer el-button-stub[type="primary"]',
    )
    expect(element.exists()).toBe(true)
    expect(element.text()).toMatch(/members.add.submit/)
  })
})

describe('props 테스트', () => {
  let wrapper
  beforeEach(() => {
    wrapper = shallowMount(InvitePane, {
      store,
      localVue,
      propsData: {
        membersTotal: 0,
        maximum: 0,
      },
    })
  })

  it('prop default 확인', () => {
    expect(wrapper.vm.$props.membersTotal).toBe(0)
    expect(wrapper.vm.$props.maximum).toBe(0)
  })
})

describe('compouted 테스트', () => {
  let wrapper
  beforeEach(() => {
    wrapper = shallowMount(InvitePane, {
      store,
      localVue,
      propsData: {
        membersTotal: 0,
        maximum: 0,
      },
    })
  })

  it('availableMember 테스트', async () => {
    expect(wrapper.vm.$data.userInfoList.length).toBe(1)

    const element = wrapper.find('MemberAddUsage')
    expect(element.attributes().availablemember).toBe('1')

    await wrapper.setData({ userInfoList: [] })
    expect(element.attributes().availablemember).toBe('0')

    await wrapper.setData({
      userInfoList: [new InviteMember(), new InviteMember()],
    })
    expect(element.attributes().availablemember).toBe('2')

    await wrapper.setProps({
      membersTotal: 5,
    })
    expect(element.attributes().availablemember).toBe('7')
  })

  it('isMaxUserAmount 테스트', () => {
    const element = wrapper.find('MemberAddUsage')
    expect(element.attributes().maximum).toBe('0')

    expect(wrapper.vm.$props.membersTotal).toBe(0)
    expect(wrapper.vm.$data.userInfoList.length).toBe(1)
    expect(wrapper.vm.availableMember).toBe(1)

    expect(wrapper.vm.isMaxUserAmount).toBeTruthy()
  })

  it('canChangeRole 테스트', () => {
    const element = wrapper.find('MemberRoleSelect')
    expect(wrapper.vm.activeWorkspace.role).toBe('MASTER')
    expect(wrapper.vm.canChangeRole).toBeTruthy()
    expect(element.attributes().disabled).toBeFalsy()
  })
})

describe('항목 추가 클릭 시 멤버 추가 테스트', () => {
  let wrapper
  beforeEach(() => {
    wrapper = shallowMount(InvitePane, {
      store,
      localVue,
      propsData: {
        membersTotal: 0,
        maximum: 10,
      },
    })
  })

  it('userInfoList 증가', async () => {
    const addMemberButton = wrapper.find(
      '.invite-pane__footer el-button-stub[type="default"]',
    )
    addMemberButton.trigger('click')
    await wrapper.vm.$nextTick()
    expect(wrapper.vm.userInfoList.length).toBe(2)
  })

  it('입력폼 추가', async () => {
    const addMemberButton = wrapper.find(
      '.invite-pane__footer el-button-stub[type="default"]',
    )
    addMemberButton.trigger('click')
    await wrapper.vm.$nextTick()
    expect(wrapper.findAllComponents({ ref: 'form' }).length).toBe(2)
  })
})
