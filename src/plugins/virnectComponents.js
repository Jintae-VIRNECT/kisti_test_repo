import Vue from 'vue'
import * as Virnect from '@virnect/components'
import '@/assets/css/_components.scss'
Object.entries(Virnect).map(([componentName, component]) => {
  Vue.component(`Virnect${componentName}`, component)
})
