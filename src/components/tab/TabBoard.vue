<template>
  <section class="tab-board">
    <board-daily> </board-daily>
    <board-monthly> </board-monthly>
  </section>
</template>

<script>
import BoardDaily from 'components/section/BoardDaily'
import BoardMonthly from 'components/section/BoardMonthly'
export default {
  name: 'TabBoard',
  components: {
    BoardDaily,
    BoardMonthly,
  },
}
</script>

<style lang="scss">
.tab-board {
  margin-top: 2px;
}
</style>
