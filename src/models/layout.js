export const sideMenus = [
  {
    path: '/',
    image: require('assets/images/icon/ic-home.svg'),
    label: 'menu.home',
  },
  {
    divider: true,
  },
  {
    path: '/members',
    collapse: 'collapseMember',
    image: require('assets/images/icon/ic-supervisor-account.svg'),
    label: 'menu.members',
  },
  {
    path: '/contents',
    collapse: 'collapseContents',
    image: require('assets/images/icon/ic-content.svg'),
    label: 'menu.content',
  },
  {
    path: '/projects',
    collapse: 'collapseProjects',
    image: require('assets/images/icon/ic-project.svg'),
    label: 'menu.projects',
  },
  {
    divider: true,
  },
  {
    path: '/tasks',
    collapse: 'collapseTask',
    image: require('assets/images/icon/ic-library-books.svg'),
    label: 'menu.tasks',
  },
]

export const sideBottomMenus = [
  {
    path: '/workspace/setting',
    image: require('assets/images/icon/ic-setting.svg'),
    label: 'menu.workspaceSetting',
  },
]

export const sideOnpremiseBottomMenus = [
  {
    path: '/workspace/setting',
    collapse: 'collapseSettings',
    image: require('assets/images/icon/ic-setting.svg'),
    label: 'menu.workspaceSetting',
  },
]
