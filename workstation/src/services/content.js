import api from '@/api/gateway'
import authService from '@/services/auth'

// model
import Content from '@/models/content/Content'
import ContentStatistics from '@/models/content/ContentStatistics'
import SceneGroup from '@/models/content/SceneGroup'
import Properties from '@/models/content/Properties'

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
    const data = await api('CONTENTS_LIST', {
      params: {
        workspaceUUID: $nuxt.$store.getters['workspace/activeWorkspace'].uuid,
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
  async getContentInfo(contentId) {
    const data = await api('CONTENT_INFO', {
      route: {
        contentUUID: contentId,
      },
    })
    return new Content(data)
  },
  /**
   * 컨텐츠 씬그룹 목록
   * @param {String} contentId
   */
  async getContentSceneGroups(contentId) {
    const data = await api('CONTENT_SCENE_GROUPS', {
      route: {
        contentUUID: contentId,
      },
    })
    return data.sceneGroupInfoList.map(sceneGroup => new SceneGroup(sceneGroup))
  },
  /**
   * 컨텐츠 속성 트리
   * @param {String} contentId
   * @param {String} userUUID
   */
  async getContentProperties(contentId, userUUID) {
    const data = await api('CONTENT_PROPERTIES', {
      route: {
        contentUUID: contentId,
      },
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
   * @param {String} contentId
   */
  async deleteContent(contentId) {
    const uuid = authService.myId
    if (!uuid) throw $nuxt.$t('messages.loginRequired')

    return await api('CONTENT_DELETE', {
      route: {
        contentUUID: contentId,
      },
      params: { uuid },
    })
  },
}
