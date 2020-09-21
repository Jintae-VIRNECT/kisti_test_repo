<template>
  <section class="workspace-license">
    <div class="workspace-license__description">
      <img src="~assets/image/workspace/img_noworkspace.svg" />
      <p
        class="workspace-license__description--title"
        v-html="$t('workspace.license_no_title')"
      ></p>
      <p
        class="workspace-license__description--sub-title"
        v-html="$t('workspace.license_no_description')"
      ></p>
    </div>
  </section>
</template>

<script>
export default {
  methods: {
    purchase() {
      location.href = window.urls.payment
    },
  },
}
</script>

<style></style>
