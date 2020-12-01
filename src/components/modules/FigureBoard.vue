<template>
  <card customClass="custom-figure-board">
    <div class="figure-board">
      <div class="figure-board--icon" :class="[type, { my: my }]">
        <img :src="imgSrc" alt="" />
      </div>

      <div class="figure-board__wrapper">
        <p class="figure-board--header">{{ header }}</p>
        <template v-if="count !== null">
          <span class="figure-board--number">{{ count }}</span>
          <span class="figure-board--text">{{
            $t('chart.collabo_count')
          }}</span>
        </template>
        <template v-else>
          <span class="figure-board--number">{{ hour }}</span>
          <span class="figure-board--text">{{ $t('chart.collabo_hour') }}</span>
          <span class="figure-board--number">{{ min }}</span>
          <span class="figure-board--text no-padding">{{
            $t('chart.collabo_minute')
          }}</span>
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
    type: {
      type: String, //daily, monthly
      defalut: 'daily',
    },
    my: {
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
.custom-figure-board {
  width: 100%;
  // width: 21.5rem;
  min-height: 8.5714rem;
}

.figure-board {
  display: flex;
  width: 100%;
  height: 100%;
  padding: 2rem 1.8571rem;
}

.figure-board__wrapper {
  margin-left: 1.5rem;
}

.figure-board--icon {
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 4.5714rem;
  height: 4.5714rem;
  border-radius: 4px;

  & > img {
    width: 2.4286rem;
    height: 2.4286rem;
  }

  &.daily {
    background: #bbc8d9;
    &.my {
      background: #0f75f5;
    }
  }

  &.monthly {
    background: #6ed6f1;
    &.my {
      background: #4f69ff;
    }
  }
}

.figure-board--header {
  color: #757f91;
  font-weight: normal;
  font-size: 1rem;
}

.figure-board--number {
  color: #0b1f48;
  font-weight: bold;
  font-size: 24px;
}

.figure-board--text {
  padding-right: 1rem;
  padding-left: 0.2857rem;
  color: #0b1f48;
  font-weight: normal;
  font-size: 1.0714rem;

  &.no-padding {
    padding-right: 0;
  }
}
</style>
