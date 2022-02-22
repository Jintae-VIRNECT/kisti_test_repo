<template>
  <div class="header-tools__calltime">
    <span>{{ callTime | timeFilter }}</span>
  </div>
</template>

<script>
export default {
  name: 'CallTime',
  props: {
    callTime: {
      type: Number,
    },
  },
}
</script>
