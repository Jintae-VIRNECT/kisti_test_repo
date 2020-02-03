<template lang="pug">
  .header__bread-crumb
    slot(name="body")
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
