const SpotControlComponent = () =>
  import(/* webpackChunkName: "SpotControl" */ 'components/spot/SpotLayout')
const SpotErrorComponent = () =>
  import(
    /* webpackChunkName: "SpotError" */ 'components/spot/SpotErrorComponent'
  )

export default [
  {
    path: '/spot-control',
    component: SpotControlComponent,
  },
  {
    path: '/spot-error',
    component: SpotErrorComponent,
  },
]
