import { api } from '@/plugins/axios'
import { store } from '@/plugins/context'

// model
import Content from '@/models/content/Content'
import ContentStatistics from '@/models/content/ContentStatistics'
import SceneGroup from '@/models/content/SceneGroup'
import Properties from '@/models/content/Properties'

function activeWorkspaceGetter() {
  return store.getters['auth/activeWorkspace']
}
function myProfileGetter() {
  return store.getters['auth/myProfile']
}

export default {
  /**
   * 컨텐츠 통계
   */
  async getContentStatistics() {
    const data = await api('CONTENTS_STATISTICS')
    return new ContentStatistics(data)
  },
  /**
   * 컨텐츠 검색
   * @param {Object} params
   */
  async searchContents(params = {}) {
    if (params.filter) {
      params.shareds = params.filter
      delete params.filter
    }
    params.sort = params.sort && params.sort.replace('contentName', 'name')

    const userUUID = params.mine && myProfileGetter().uuid
    const workspaceUUID = activeWorkspaceGetter().uuid

    const data = await api('CONTENTS_LIST', {
      params: {
        converteds: 'NO',
        workspaceUUID,
        userUUID,
        size: 10,
        ...params,
      },
    })
    return {
      list: data.contentInfo.map(content => new Content(content)),
      total: data.pageMeta.totalElements,
    }
  },
  /**
   * 컨텐츠 상세 정보
   * @param {String} contentId
   */
  async getContentInfo(contentUUID) {
    const data = await api('CONTENT_INFO', {
      route: { contentUUID },
    })
    return new Content(data)
  },
  /**
   * 컨텐츠 씬그룹 목록
   * @param {String} contentId
   */
  async getContentSceneGroups(contentUUID) {
    const data = await api('CONTENT_SCENE_GROUPS', {
      route: { contentUUID },
    })
    return data.sceneGroupInfoList.map(sceneGroup => new SceneGroup(sceneGroup))
  },
  /**
   * 컨텐츠 속성 트리
   * @param {String} contentUUID
   * @param {String} userUUID
   */
  async getContentProperties(contentUUID, userUUID) {
    const data = await api('CONTENT_PROPERTIES', {
      route: { contentUUID },
      params: { userUUID },
    })
    return [
      {
        id: data.contentUUID,
        label: data.contentName,
        children: new Properties(data.propertiesMetadata).tree(),
      },
    ]
  },
  /**
   * 컨텐츠 삭제
   * @param {Array} contentUUIDs
   */
  async deleteContent(contentUUIDs) {
    const data = await api('CONTENT_DELETE', {
      params: {
        contentUUIDs,
        workspaceUUID: activeWorkspaceGetter().uuid,
      },
    })
    const errors = data.deleteResponseList.filter(res => !res.result)
    if (errors.length) throw errors
  },
  /**
   * 컨텐츠 상태 변경
   * @param {String} contentUUID
   * @param {Object} form
   */
  async updateContent(contentUUID, form) {
    return await api('CONTENT_UPDATE', {
      route: { contentUUID },
      params: {
        contentUUID,
        userUUID: myProfileGetter().uuid,
        ...form,
      },
    })
  },
}
