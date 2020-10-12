<template>
  <section class="board-monthly">
    <div class="board-monthly__header">
      <span class="board-monthly__header--title">월별 협업</span>
      <span class="board-monthly__header--description">
        설정한 기간의 협업 내용을 보여줍니다.
      </span>
      <datepicker
        class="board-daily__header--datepicker"
        :pickerName="'monthly'"
        :minimumView="'month'"
        :maximumView="'month'"
        :format="'yyyy-MM'"
      ></datepicker>
    </div>
    <div class="board-monthly__body">
      <card :customClass="'custom-card-chart'"><div>차트 들어가요</div></card>
      <card-board
        :count="5"
        :countDescription="'월별 총 협업 수'"
        :time="36555"
        :timeDescription="'월별 총 협업 시간'"
      ></card-board>
    </div>
  </section>
</template>

<script>
import Card from 'Card'
import CardBoard from 'CardBoard'
import Datepicker from 'Datepicker'
export default {
  name: 'BoardMonthly',
  components: {
    Card,
    CardBoard,
    Datepicker,
  },
}
</script>

<style lang="scss">
.board-monthly {
  height: 371px;
  margin-top: 40px;
}
.board-monthly__header {
  position: relative;
  height: 60px;
}

.board-monthly__header--title {
  margin-right: 10px;
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 22px;
}

.board-monthly__header--description {
  color: rgb(122, 122, 122);
  font-weight: 500;
  font-size: 15px;
}

.board-monthly__header--datepicker {
  &.datepicker {
    position: absolute;
    top: 0px;
    right: 0px;
  }
}

.board-monthly__body {
  display: flex;
}

.custom-card-chart {
  width: 968px;
  height: 310px;
  margin-right: 12px;
}
</style>
