<template>
  <li class="sharing-image">
    <div class="sharing-image__item loading">
      <img
        class="upload-loading"
        src="~assets/image/call/ic_loading_spinner_small.svg"
        alt="upload-loading"
      />
    </div>
    <p class="sharing-image__name">{{ fileName }}</p>
  </li>
</template>

<script>
export default {
  name: 'SharingFileSpinner',
  props: {
    fileName: {
      type: String,
      default: '',
    },
  },
}
</script>

<style></style>
