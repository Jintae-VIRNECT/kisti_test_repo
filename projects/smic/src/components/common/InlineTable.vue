<template lang="pug">
  .card
    .card__header(v-if="setMainHeader")
      .card__header--left
        slot(name="header-left")
      .card__header--right
        slot(name="header-right")
    .card__header--secondary(v-if="setSubHeader")
      slot(name="header--secondary")
    .card__body
      div
        slot(name="tabs")
      div
        slot(name="body")
    .card__foot
      slot(name="foot")
</template>
<script>
export default {
  props: {
    setMainHeader: Boolean,
    setSubHeader: Boolean,
  },
}
</script>
