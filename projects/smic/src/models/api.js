export default {
  // auth
  USER_LOGIN: () => '/users/login',
  USER_LOGOUT: () => '/users/logout',
  // members
  GET_MEMBER_LIST: () => '@workspace/members',
  // contents
  GET_CONTENTS_LIST: () => '@contents',
  DELETE_CONTENT: id => `@contents/${id}`,
  // process
  GET_PROCESS_LIST: () => '',
  // scene group
  GET_SCENE_GROUP_LIST: () => '@contents/metadata/sceneGroups',
  // issue
  GET_ISSUE_LIST: () => '',
}
