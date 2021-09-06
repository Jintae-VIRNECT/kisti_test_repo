import { api } from '@/plugins/axios'
import { store } from '@/plugins/context'

// model
import Project from '@/models/project/Project'

function activeWorkspaceGetter() {
  return store.getters['auth/activeWorkspace']
}
function myProfileGetter() {
  return store.getters['auth/myProfile']
}

export default {
  /**
   * @description 프로젝트 검색 조회 service
   * @param {Object} params
   * @returns {Object} {list, total}
   */
  async searchProjects(params = {}) {
    // 4개의 필터 값을 가져와, API 전달할 파라미터 값으로 작업 후 filter 프로퍼티 삭제.
    if (params.filter) {
      params.targetType = params.filter.targetFilter
      params.mode = params.filter.modeFilter
      params.sharePermission = params.filter.sharedTypes
      params.editPermission = params.filter.editTypes
      delete params.filter
    }

    const userUUID = myProfileGetter().uuid
    const workspaceUUID = activeWorkspaceGetter().uuid

    // ALL값을 가진 filter 파라미터 값은 삭제 후 API를 요청한다.
    Object.keys(params).forEach(key => {
      if (params[key] == 'ALL') delete params[key]
    })

    const data = await api('PROJECTS_LIST', {
      params: {
        workspaceUUID,
        userUUID,
        size: 10,
        ...params,
      },
    })

    return {
      list: data.projectInfoList.map(project => new Project(project)),
      total: data.pageMeta.totalElements,
    }
  },
  /**
   * @description 프로젝트 상세정보 조회 service
   * @param {String} projectId
   * @returns {Object} Project
   */
  async getProjectInfo(projectUUID) {
    const data = await api('PROJECT_INFO', {
      route: { projectUUID },
    })
    return new Project(data)
  },
}
