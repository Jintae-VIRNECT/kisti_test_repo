<template>
  <section class="workspace-license">
    <div class="workspace-license__description">
      <p class="workspace-license__description--title">
        <!-- 선택하신 워크스페이스에 -->
        {{ $t('workspace.license_no_title') }}
      </p>
      <p class="workspace-license__description--sub-title">
        {{ $t('workspace.license_no_description') }}
      </p>
    </div>
  </section>
</template>

<script>
export default {
  methods: {
    purchase() {
      location.href = window.urls.pay
    },
  },
}
</script>

<style></style>
