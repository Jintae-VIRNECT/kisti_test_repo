import api from '@/api/gateway'
import { Content } from '@/models/content'
import { SceneGroup } from '@/models/sceneGroup'

export default {
  /**
   * 컨텐츠 페이지 초기 리스트
   */
  async getDefaultContentsList() {
    const data = await api('CONTENTS_LIST', {
      params: {
        size: 20,
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
  async getSceneGroupList(contentId) {
    const data = await api('SCENE_GROUP_LIST', {
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
    const uuid = $nuxt.$store.getters['user/getUser'].uuid
    if (!uuid) {
      throw '로그인 필요'
    }
    return await api('CONTENT_DELETE', {
      route: {
        contentUUID: contentId,
      },
      params: { uuid },
    })
  },
}
