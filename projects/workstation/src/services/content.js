import api from '@/api/gateway'
import authService from '@/services/auth'

// model
import { Content, ContentStatistics } from '@/models/content'
import { SceneGroup } from '@/models/sceneGroup'

export default {
  /**
   * 컨텐츠 통계
   */
  async getContentStatistics() {
    const data = await api('CONTENTS_STATISTICS')
    return ContentStatistics(data)
  },
  /**
   * 컨텐츠 검색
   * @param {Object} params
   */
  async searchContents(params) {
    const data = await api('CONTENTS_LIST', {
      params: {
        size: 10,
        ...params,
      },
    })
    return data.contentInfo.map(content => Content(content))
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
    return Content(data)
  },
  /**
   * 컨텐츠 씬그룹 정보
   * @param {String} contentId
   */
  async getSceneGroupsList(contentId) {
    const data = await api('SCENE_GROUPS_LIST', {
      params: {
        contentUUID: contentId,
      },
    })
    return data.sceneGroupInfoList.map(sceneGroup => SceneGroup(sceneGroup))
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
