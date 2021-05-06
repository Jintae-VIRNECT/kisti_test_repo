import { shallowMount } from '@vue/test-utils'
import FileTable from 'components/modules/FileTable'
import localVue from '__utils__/localVue'

import i18n from 'plugins/remote/i18n'
import store from 'store'

describe('FileTable.vue', () => {
  const emptyText = '데이터 없음'
  const columns = [['filename'], ['duration'], ['size']]
  const datas = [
    {
      createAt: '2021-01-20T02:45:01.649Z',
      duration: 31,
      filename: '2021-01-20_11-44-28_ses_WZplKWDsLg.mp4',
      framerate: 20,
      recordingId: 'fe3b183d-54af-4c1b-a9b1-96c1f3c46bb7',
      resolution: '1920x1080',
      sessionId: 'ses_WZplKWDsLg',
      size: 4393469,
      userId: '40247ff4cbe04a1e8ae3203298996f4c',
      workspaceId: '47cfc8b44fad3d0bbdc96d5307c6370a',
    },
  ]
  const headers = ['파일명', '녹화 시간', '파일 용량', '녹화 재생']

  const showPlayButton = true
  const showToggleHeader = true
  const type = 'server'

  store.getters.workspace = { uuid: '' }

  it('아무 데이터가 없으면 props로 전달된 emptyText출력', () => {
    const wrapper = shallowMount(FileTable, {
      localVue,
      i18n,
      propsData: {
        emptyText: emptyText,
      },
    })
    expect(wrapper.text().includes(emptyText)).toEqual(true)
  })

  it('헤더 렌더링 확인', () => {
    const wrapper = shallowMount(FileTable, {
      localVue,
      store,
      i18n,
      propsData: {
        emptyText: emptyText,
        columns: columns,
        datas: datas,
        headers: headers,
        showPlayButton: showPlayButton,
        showToggleHeader: showToggleHeader,
        type: type,
      },
    })

    const text = wrapper.text()

    //check header rendered
    expect(
      headers.every(header => {
        return text.includes(header)
      }),
    ).toEqual(true)
  })
  it('columns에 정의되지 않은 데이터는 출력되면 안됨', () => {
    const wrapper = shallowMount(FileTable, {
      localVue,
      store,
      i18n,
      propsData: {
        emptyText: emptyText,
        columns: columns,
        datas: datas,
        headers: headers,
        showPlayButton: showPlayButton,
        showToggleHeader: showToggleHeader,
        type: type,
      },
    })
    expect(wrapper.text().includes(datas[0].resolution)).toEqual(false)
  })

  it('columns에 정의된 데이터는 출력되어야함', () => {
    const wrapper = shallowMount(FileTable, {
      localVue,
      store,
      i18n,
      propsData: {
        emptyText: emptyText,
        columns: columns.concat([['recordingId']]),
        datas: datas,
        headers: headers,
        showPlayButton: showPlayButton,
        showToggleHeader: showToggleHeader,
        type: type,
      },
    })
    expect(wrapper.text().includes(datas[0].recordingId)).toEqual(true)
  })
})
