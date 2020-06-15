import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'

export const install = [
  {
    label: 'VIRNECT Make 설치',
    path: `${urls.download[process.env.TARGET_ENV]}/make`,
    image: require('assets/images/logo/logo-make.svg'),
  },
  {
    label: 'VIRNECT View 설치',
    path: `${urls.download[process.env.TARGET_ENV]}/view`,
    image: require('assets/images/logo/logo-view.svg'),
  },
]

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
