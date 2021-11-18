import Vue from 'vue'
import { config } from '@vue/test-utils'

config.mocks['$t'] = msg => msg
// global.matchMedia =
//   global.matchMedia ||
//   function () {
//     return {
//       matches: false,
//       addListener: function () {},
//       removeListener: function () {},
//     }
//   }

import * as Virnect from '@virnect/components/dist/index'
Object.entries(Virnect).map(([componentName, component]) => {
  Vue.component(`Virnect${componentName}`, component)
})
