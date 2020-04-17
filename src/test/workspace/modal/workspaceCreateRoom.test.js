// Vue 및 테스트할 컴포넌트 가져오기
import Vue from 'vue'
import WorkspaceCreateRoom from 'src/components/workspace/modal/WorkspaceCreateRoom'

// Jasmine 2.0 테스트는 다음과 같습니다.
// 원하는 테스트 러너 / 테스트 라이브러리를 사용하십시오
describe('WorkspaceCreateRoom', () => {
  // 원시 컴포넌트 옵션을 검사합니다.
  // it('has a created hook', () => {
  //   expect(typeof WorkspaceCreateRoom.created).toBe('function')
  // })

  // 원시 컴포넌트 옵션에서 함수 결과를 테스트합니다.
  // it('sets the correct default data', () => {
  //   expect(typeof WorkspaceCreateRoom.data).toBe('function')
  //   const defaultData = WorkspaceCreateRoom.data()
  //   expect(defaultData.message).toBe('hello!')
  // })

  // 마운트 할 때 컴포넌트 인스턴스를 검사합니다.
  it('correctly sets the message when created', () => {
    const vm = new Vue(WorkspaceCreateRoom).$mount()
    expect(vm.message).toBe('bye!')
  })

  // 인스턴스를 마운트하고 출력된 결과를 검사합니다.
  // it('renders the correct message', () => {
  //   const Constructor = Vue.extend(WorkspaceCreateRoom)
  //   const vm = new Constructor().$mount()
  //   expect(vm.$el.textContent).toBe('bye!')
  // })
})
