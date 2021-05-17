<template>
  <section class="workspace-license">
    <div class="workspace-license__description">
      <img src="~assets/image/workspace/img_noworkspace.svg" />
      <p
        class="workspace-license__description--title"
        v-html="
          isOnpremise
            ? $t('workspace.license_no_title_onpremise')
            : $t('workspace.license_no_title')
        "
      ></p>
      <p
        class="workspace-license__description--sub-title"
        v-html="
          isOnpremise
            ? $t('workspace.license_no_description_onpremise')
            : $t('workspace.license_no_description')
        "
      ></p>
    </div>
  </section>
</template>

<script>
import { URLS } from 'configs/env.config'
export default {
  methods: {
    purchase() {
      location.href = URLS['pay']
    },
  },
}
</script>

<style></style>
