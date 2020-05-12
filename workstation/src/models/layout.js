export const sideLogo = {
  path: '@collapseWorkspace',
  image: require('assets/images/workspace-profile.png'),
  label: 'Workspaces',
}

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
    image: require('assets/images/icon/ic-supervisor-account.svg'),
    label: 'menu.members',
  },
  {
    path: '/contents',
    image: require('assets/images/icon/ic-description.svg'),
    label: 'menu.contents',
  },
  // {
  //   divider: true,
  // },
  // {
  //   path: '/processes',
  //   image: require('assets/images/icon/ic-library-books.svg'),
  //   label: 'processes',
  // },
]

export const sideBottomMenus = [
  {
    path: '/workspace/setting',
    image: require('assets/images/icon/ic-setting.svg'),
    label: 'menu.workspaceSetting',
  },
]
