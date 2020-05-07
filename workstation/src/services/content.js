import api from '@/api/gateway'
import authService from '@/services/auth'

// model
import Content from '@/models/content/Content'
import ContentStatistics from '@/models/content/ContentStatistics'
import SceneGroup from '@/models/content/SceneGroup'

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
  async getSceneGroupsList(contentId) {
    const data = await api('SCENE_GROUPS_LIST', {
      params: {
        contentUUID: contentId,
      },
    })
    return data.sceneGroupInfoList.map(sceneGroup => new SceneGroup(sceneGroup))
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
