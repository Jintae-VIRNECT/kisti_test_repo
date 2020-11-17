<template>
  <section class="tab-collabo">
    <collabo-search-bar></collabo-search-bar>
    <collabo-history-list></collabo-history-list>
  </section>
</template>

<script>
import CollaboSearchBar from 'components/collabo/section/CollaboSearchBar'
import CollaboHistoryList from 'components/collabo/section/CollaboHistoryList'
export default {
  name: 'TabCollabo',
  components: {
    CollaboSearchBar,
    CollaboHistoryList,
  },
}
</script>

<style lang="scss"></style>
