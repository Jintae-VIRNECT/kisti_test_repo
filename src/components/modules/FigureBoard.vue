<template>
  <card customClass="custom-card-figure-board">
    <div class="figure-board">
      <div class="figure-board--icon" :class="{ 'only-me': onlyMe }">
        <img :src="imgSrc" alt="" />
      </div>

      <div class="figure-board__wrapper">
        <p class="figure-board--header">{{ header }}</p>
        <template v-if="count">
          <span class="figure-board--number">{{ count }}</span>
          <span class="figure-board--text">회</span>
        </template>
        <template v-else>
          <span class="figure-board--number">{{ hour }}</span>
          <span class="figure-board--text">시간</span>
          <span class="figure-board--number">{{ min }}</span>
          <span class="figure-board--text">분</span>
        </template>
      </div>
    </div>
  </card>
</template>

<script>
import Card from 'Card'
export default {
  name: 'FigureBoard',
  components: {
    Card,
  },
  props: {
    header: {
      type: String,
      default: '',
    },
    count: {
      type: Number,
      default: null,
    },
    time: {
      type: Number,
      default: null,
    },
    imgSrc: {
      type: String,
      default: null,
    },
    onlyMe: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    hour() {
      let sec_num = Number.parseInt(this.time, 10)
      let hours = Math.floor(sec_num / 3600)
      return hours < 10 ? `0${hours}` : hours
    },
    min() {
      let sec_num = Number.parseInt(this.time, 10)
      let hours = Math.floor(sec_num / 3600)
      let minutes = Math.floor((sec_num - hours * 3600) / 60)
      return minutes < 10 ? `0${minutes}` : minutes
    },
  },
}
</script>

<style lang="scss">
.figure-board {
  display: flex;
  width: 100%;
  height: 100%;
  padding: 28px 26px;
}

.figure-board__wrapper {
  margin-left: 21px;
}

.figure-board--icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  background: #bbc8d9;
  border-radius: 4px;

  &.only-me {
    background: rgb(15, 117, 245);
  }

  & > img {
    width: 34px;
    height: 34px;
  }
}

.custom-card-figure-board {
  width: 308px;
  min-height: 120px;
}

.figure-board--header {
  color: rgb(117, 127, 145);
  font-weight: 500;
  font-size: 14px;
}

.figure-board--number {
  color: #0b1f48;
  font-weight: bold;
  font-size: 28px;
}

.figure-board--text {
  padding-right: 14px;
  padding-left: 4px;
  color: #0b1f48;
  font-weight: normal;
  font-size: 15px;
}
</style>
