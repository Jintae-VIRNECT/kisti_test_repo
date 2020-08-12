<template>
  <section class="workspace-license">
    <div class="workspace-license__description expire">
      <p class="workspace-license__description--title">
        {{ $t('workspace.license_expire_title') }}
      </p>
      <p class="workspace-license__description--sub-title">
        {{ $t('workspace.license_expire_description') }}
      </p>
      <button
        class="workspace-license__description--purchase btn"
        @click="purchase"
      >
        {{ $t('button.license_purchase') }}
      </button>
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
