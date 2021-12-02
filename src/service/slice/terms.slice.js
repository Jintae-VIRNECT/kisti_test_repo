import { ref, computed, watch } from '@vue/composition-api'

export default function terms() {
  const allTerms = ref(null)
  const serviceAgree = ref(false)
  const privacyAgree = ref(false)
  const marketingAgree = ref(false)

  const checkAll = computed(() => {
    if (serviceAgree.value && privacyAgree.value && marketingAgree.value) {
      return true
    } else return false
  })

  watch(
    () => allTerms.value,
    value => {
      if (
        value &&
        !serviceAgree.value &&
        !privacyAgree.value &&
        !marketingAgree.value
      ) {
        serviceAgree.value = true
        privacyAgree.value = true
        marketingAgree.value = true
      }
      if (
        !value &&
        serviceAgree.value &&
        privacyAgree.value &&
        marketingAgree.value
      ) {
        serviceAgree.value = false
        privacyAgree.value = false
        marketingAgree.value = false
      }
      if (
        value &&
        (serviceAgree.value || privacyAgree.value || marketingAgree.value)
      ) {
        serviceAgree.value = true
        privacyAgree.value = true
        marketingAgree.value = true
      }
      if (
        !value ||
        !serviceAgree.value ||
        !privacyAgree.value ||
        !marketingAgree.value
      ) {
        allTerms.value = false
      }
    },
  )

  watch(
    () => serviceAgree.value,
    value => {
      if (!value) allTerms.value = false
      if (value && privacyAgree.value && marketingAgree.value) {
        allTerms.value = true
      }
      // if (privacyAgree.value && value) root.$store.commit('POLICY_AGREE', value)
      // else root.$store.commit('POLICY_AGREE', false)
    },
  )

  watch(
    () => privacyAgree.value,
    value => {
      if (!value) allTerms.value = false
      if (serviceAgree.value && value && marketingAgree.value) {
        allTerms.value = true
      }
      // if (serviceAgree.value && value) root.$store.commit('POLICY_AGREE', value)
      // else root.$store.commit('POLICY_AGREE', false)
    },
  )

  watch(
    () => marketingAgree.value,
    value => {
      if (!value) allTerms.value = false
      if (serviceAgree.value && privacyAgree.value && value) {
        allTerms.value = true
      }
      // root.$store.commit('MARKETING_RECEIVE', value)
    },
  )

  return {
    allTerms,
    serviceAgree,
    privacyAgree,
    marketingAgree,
    checkAll,
  }
}
