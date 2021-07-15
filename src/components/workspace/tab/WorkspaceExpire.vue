<template>
  <section class="workspace-license">
    <div class="workspace-license__description expire">
      <img src="~assets/image/workspace/img_licenseexpire.svg" />
      <p
        class="workspace-license__description--title"
        v-html="
          isOnpremise
            ? $t('workspace.license_expire_title_onpremise')
            : $t('workspace.license_expire_title')
        "
      ></p>
      <p
        class="workspace-license__description--sub-title"
        v-html="$t('workspace.license_expire_description')"
      ></p>
      <button
        v-if="!isOnpremise"
        class="workspace-license__description--purchase btn"
        @click="purchase"
      >
        {{ $t('button.license_purchase') }}
      </button>
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
