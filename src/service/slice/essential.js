import { ref, computed } from '@vue/composition-api'

export default function essential(root) {
  const logo = ref({})
  const customInfo = computed(() => {
    return root.$store.getters.customInfo
  })

  const isMobile = str => {
    root.$store.commit('IS_MOBILE', str)
  }

  return {
    logo,
    customInfo,
    isMobile,
  }
}
