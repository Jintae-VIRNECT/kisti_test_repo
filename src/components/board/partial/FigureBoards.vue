<template>
  <div class="figure-boards">
    <figure-board
      v-for="(boardInfo, index) in figureBoardInfos"
      :my="boardInfo.my"
      :header="boardInfo.header"
      :time="boardInfo.time"
      :count="boardInfo.count"
      :imgSrc="boardInfo.imgSrc"
      :key="index"
      :type="boardInfo.type"
    ></figure-board>
  </div>
</template>

<script>
import FigureBoard from 'FigureBoard'
export default {
  name: 'FigureBoards',
  components: {
    FigureBoard,
  },
  props: {
    figureBoardInfos: {
      type: Array,
      default: () => [],
      require: true,
    },
  },
}
</script>
<style lang="scss">
@import '~assets/style/mixin';
.figure-boards {
  display: grid;
  margin-top: 1.2857rem;

  grid-template-columns: 1fr 1fr;
  gap: 1.1429rem 1.1429rem;
}
</style>
