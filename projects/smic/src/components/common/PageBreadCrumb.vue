<template lang="pug">
  .header__bread-crumb
    span {{title}}
</template>
<script>
export default {
  props: {
    title: {
      type: String,
    },
  },
}
</script>
