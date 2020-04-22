import Workspace from '@/models/workspace/Workspace'

function getMyWorkspaces() {
  return process.client && [...$nuxt.$store.getters['auth/myWorkspaces']]
}

export default {
  getMyWorkspaces,
  getWorkspaceList() {
    const data = [0, 1, 2, 3, 4]
    return data.map(workspace => new Workspace(workspace))
  },
}
