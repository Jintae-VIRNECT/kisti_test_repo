<template>
  <card :customClass="'custom-card-board'">
    <div class="card-board">
      <div class="card-board__wrapper">
        <p class="card-board--description">{{ countDescription }}</p>
        <div>
          <span class="card-board--number">{{ count }}</span>
          <span class="card-board--text">회</span>
        </div>
      </div>
      <div class="card-board__wrapper">
        <p class="card-board--description">{{ timeDescription }}</p>
        <div>
          <span class="card-board--number">{{ hour }}</span>
          <span class="card-board--text">시간</span>
          <span class="card-board--number">{{ min }}</span>
          <span class="card-board--text">분</span>
        </div>
      </div>
    </div>
  </card>
</template>

<script>
import Card from 'Card'
export default {
  name: 'CardBoard',
  components: {
    Card,
  },
  props: {
    count: {
      type: Number,
      default: 0,
    },
    countDescription: {
      type: String,
      default: null,
    },
    time: {
      type: Number,
      default: null,
    },
    timeDescription: {
      type: String,
      default: null,
    },
  },
  computed: {
    hour() {
      return this.$dayjs(this.time * 1000)
        .utc()
        .format('hh')
    },
    min() {
      return this.$dayjs(this.time * 1000)
        .utc()
        .format('mm')
    },
  },
  methods: {},
}
</script>

<style lang="scss">
.custom-card-board {
  width: 300px;
  height: 310px;
}

.card-board {
  display: grid;
  height: 100%;
  padding: 23px;
  text-align: center;
}

.card-board__wrapper {
  display: flex;
  flex-direction: column;
  align-content: center;
  justify-content: center;
}

.card-board__wrapper:first-of-type {
  border-bottom: 1px solid rgb(227, 227, 227);
}

.card-board--number {
  padding-right: 6px;
  padding-left: 13px;
  color: #0b1f48;
  font-weight: bold;
  font-size: 38px;
}

.card-board--description {
  color: #757f91;
  font-weight: 500;
  font-size: 15px;
}

.card-board--text {
  color: #0b1f48;
  font-weight: normal;
  font-size: 18px;
}
</style>
