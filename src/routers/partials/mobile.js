const MobileHelp = () => import('components/mobileApp/MobileAppHelp')

export default [
  {
    path: '',
    name: 'appMenu',
    component: MobileHelp,
  },
  {
    path: '*',
    redirect: '/m',
  },
]
