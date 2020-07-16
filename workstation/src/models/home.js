export function install(app) {
  return [
    {
      label: app.$t('home.install.remote'),
      path: `${app.$url.download}/remote`,
      image: require('assets/images/logo/logo-remote.svg'),
    },
    {
      label: app.$t('home.install.make'),
      path: `${app.$url.download}/make`,
      image: require('assets/images/logo/logo-make.svg'),
    },
    {
      label: app.$t('home.install.view'),
      path: `${app.$url.download}/view`,
      image: require('assets/images/logo/logo-view.svg'),
    },
  ]
}

export const guide = [
  {
    label: '워크스페이스 관리자용 가이드',
    path: '',
  },
  {
    label: '워크스페이스 사용자용 가이드',
    path: '',
  },
  {
    label: 'Make 사용자 가이드',
    path: '',
  },
  {
    label: 'View 사용자 가이드',
    path: '',
  },
  {
    label: 'VIRNECT 플랜 구매 가이드',
    path: '',
  },
]
