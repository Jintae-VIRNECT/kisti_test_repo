const SpotControlComponent = () =>
  import(/* webpackChunkName: "SpotControl" */ 'components/spot/SpotLayout')
const SpotErrorComponent = () =>
  import(
    /* webpackChunkName: "SpotError" */ 'components/spot/SpotErrorComponent'
  )
import { SPOT_CONTROL_ACTIVE } from 'configs/env.config'
import { RUNTIME, RUNTIME_ENV } from 'configs/env.config'

export default [
  {
    path: '/spot-control',
    component: SpotControlComponent,
    //구축형 or spot 활성화되있지 않는 경우 redirect 활성화
    redirect:
      RUNTIME.ONPREMISE === RUNTIME_ENV && SPOT_CONTROL_ACTIVE
        ? null
        : { name: 'workspace' },
  },
  {
    path: '/spot-error',
    component: SpotErrorComponent,
    //구축형 or spot 활성화되있지 않는 경우 redirect 활성화
    redirect:
      RUNTIME.ONPREMISE === RUNTIME_ENV && SPOT_CONTROL_ACTIVE
        ? null
        : { name: 'workspace' },
  },
]
