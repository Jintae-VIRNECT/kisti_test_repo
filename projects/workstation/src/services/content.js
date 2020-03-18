import api from '@/api/gateway'
import authService from '@/services/auth'

// model
import { Content } from '@/models/content'
import { SceneGroup } from '@/models/sceneGroup'

async function getContentsList(params) {
  const data = await api('CONTENTS_LIST', {
    params: {
      ...params,
      size: 10,
    },
  })
  return data.contentInfo.map(content => Content(content))
}

export default {
  /**
   * 컨텐츠 통계
   */
  async getContentsStatistics() {
    const data = await api('CONTENTS_STATISTICS')
    return data
  },
  /**
   * 컨텐츠 페이지 초기 리스트
   */
  async getDefaultContentsList() {
    return await getContentsList()
  },
  /**
   * 컨텐츠 검색
   */
  async searchContents(params) {
    return await getContentsList(params)
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
