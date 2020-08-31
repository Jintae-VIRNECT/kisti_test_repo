<template>
  <section class="search">
    <span class="search--label">기간 검색</span>
    <!-- 캘린더 컴포넌트 -->
    <div class="search-calendar">
      <datepicker :pickerName="'from'" :highlighted="highlighted"></datepicker>
      <calendar-button
        :pickerName="'from'"
        :isActive="buttonStatus.from"
      ></calendar-button>
    </div>
    <span class="search-calendar--tilde"> <!-- 007E Tilde 표시 -->~ </span>
    <div class="search-calendar">
      <datepicker :pickerName="'to'" :highlighted="highlighted"></datepicker>
      <calendar-button
        :pickerName="'to'"
        :isActive="buttonStatus.to"
      ></calendar-button>
    </div>
    <div class="search__wrapper">
      <input class="search--input" type="text" placeholder="사용자 이름" />
      <button class="search--button">검색</button>
    </div>
  </section>
</template>

<script>
import CalendarButton from './partials/RemoteCalendarButton'
import Datepicker from 'components/modules/Datepicker'
export default {
  name: 'RemoteSearchBar',

  components: {
    CalendarButton,
    Datepicker,
  },
  data() {
    return {
      highlighted: {
        from: null,
        to: null,
      },
      buttonStatus: {
        from: false,
        to: false,
      },
    }
  },
  methods: {
    setHighlighting(dateInfo) {
      this.highlighted[dateInfo.pickerName] = new Date(dateInfo.date)
    },
    active(pickerName) {
      this.buttonStatus[pickerName] = true
    },
    deActive(pickerName) {
      this.buttonStatus[pickerName] = false
    },
  },
  mounted() {
    this.$eventBus.$on('update::datepicker', this.setHighlighting)
    this.$eventBus.$on('open::calendar', this.active)
    this.$eventBus.$on('closed::calendar', this.deActive)
  },
  beforeDestroy() {
    this.$eventBus.$off('update::datepicker')
    this.$eventBus.$off('open::calendar')
    this.$eventBus.$off('closed::calendar')
  },
}
</script>

<style lang="scss">
.search {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 7.1429rem;
  margin: 2.4286rem 0 1.8571rem 0;
  background: #f5f7fa;
  border-radius: 0px;
}

.search--label {
  margin-right: 1.2857rem;
  color: #0b1f48;
  font-weight: 500;
  font-size: 1rem;
}

.search__wrapper {
  display: flex;
  width: 31.1429rem;
  height: 3.4286rem;
  margin-left: 5.5rem;
}

.search--input {
  width: 23.7143rem;
  background: #ffffff;
  border: 1px solid #e5e7e9;
  border-radius: 0px;
  &::placeholder {
    padding-left: 1.1429rem;
    color: #bac2cc;
    font-weight: 500;
    font-size: 1.1429rem;
  }
}

.search--button {
  width: 7.4286rem;
  height: 3.4286rem;
  background: #145ab6;
  border-radius: 0px;
}

.search-calendar {
  display: flex;
}

.search-calendar--selected-date {
  width: 9.7857rem;
  height: 3.4286rem;
  padding: 0.7143rem 0 1rem 1.4286rem;
  background: #ffffff;
  border: 1px solid #e5e7e9;
  border-right: none;
  & > p {
    color: #757f91;
    font-weight: 500;
    font-size: 1.1429rem;
  }
}

.search-calendar--tilde {
  width: 0.5714rem;
  height: 1.4286rem;
  margin: 0 0.7143rem;
  color: #757f91;
  font-weight: 500;
  font-size: 1rem;
}
</style>
