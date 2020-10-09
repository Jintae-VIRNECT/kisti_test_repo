<template>
  <section class="board-daily">
    <div class="board-daily__header">
      <span class="board-daily__header--title">일일 협업</span>
      <span class="board-daily__header--description">
        설정한 날짜의 일일 협업 내용을 보여줍니다.
      </span>
      <datepicker
        class="board-daily__header--datepicker"
        :pickerName="'daily'"
        :highlighted="{}"
        :minimumView="'day'"
        :maximumView="'day'"
      ></datepicker>
    </div>
    <div class="board-daily__body">
      <card :customClass="'custom-card-chart'"><div>차트 들어가요</div></card>
      <card-board
        :count="5"
        :countDescription="'일별 총 협업 수'"
        :time="5219611"
        :timeDescription="'일별 총 협업 시간'"
      ></card-board>
    </div>
  </section>
</template>

<script>
import Card from 'Card'
import CardBoard from 'CardBoard'
import Datepicker from 'Datepicker'
export default {
  name: 'BoardDaily',
  components: {
    Card,
    CardBoard,
    Datepicker,
  },
}
</script>

<style lang="scss">
.board-daily {
  height: 371px;
  margin-top: 40px;
}

.board-daily__header {
  position: relative;
  height: 60px;
}

.board-daily__header--title {
  margin-right: 10px;
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 22px;
}

.board-daily__header--description {
  color: rgb(122, 122, 122);
  font-weight: 500;
  font-size: 15px;
}

.board-daily__header--datepicker {
  &.datepicker {
    position: absolute;
    top: 0px;
    right: 0px;
  }
}

.board-daily__body {
  display: flex;
}

.custom-card-chart {
  width: 968px;
  height: 310px;
  margin-right: 12px;
}
</style>
