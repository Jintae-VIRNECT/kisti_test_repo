import Workspace from '@/models/workspace/Workspace'

export default {
  getWorkspaceList() {
    const data = [0, 1, 2, 3, 4]
    return data.map(workspace => new Workspace(workspace))
  },
}
