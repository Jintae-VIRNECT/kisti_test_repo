<template>
  <section class="search">
    <span class="search--label">기간 검색</span>
    <!-- 캘린더 컴포넌트 -->
    <div class="search-calender">
      <datepicker :pickerName="'from'" :highlighted="highlighted"></datepicker>
      <calendar-button :isActive="buttonStatus.from"></calendar-button>
    </div>
    <span class="search-calender--tilde"> <!-- 007E Tilde 표시 -->~ </span>
    <div class="search-calender">
      <datepicker :pickerName="'to'" :highlighted="highlighted"></datepicker>
      <calendar-button :isActive="buttonStatus.to"></calendar-button>
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
      console.log(pickerName)
      this.buttonStatus[pickerName] = false
    },
  },
  mounted() {
    this.$eventBus.$on('update::datepicker', this.setHighlighting)
    this.$eventBus.$on('open::calendar', this.active)
    this.$eventBus.$on('close::calendar', this.deActive)
  },
  beforeDestroy() {
    this.$eventBus.$off('update::datepicker')
  },
}
</script>

<style lang="scss">
.search {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100px;
  margin: 34px 0 26px 0;
  background: #f5f7fa;
  border-radius: 0px;
}

.search--label {
  margin-right: 18px;
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 14px;
}

.search__wrapper {
  display: flex;
  width: 436px;
  height: 48px;
  margin-left: 77px;
}

.search--input {
  width: 332px;
  background: #ffffff;
  border: 1px solid #e5e7e9;
  border-radius: 0px;
  &::placeholder {
    padding-left: 16px;
    color: rgb(186, 194, 204);
    font-weight: 500;
    font-size: 16px;
  }
}

.search-calender {
  display: flex;
}

.search--button {
  width: 104px;
  height: 48px;
  background: rgb(20, 90, 182);
  border-radius: 0px;
}

.search-calender--selected-date {
  width: 137px;
  height: 48px;
  padding: 10px 0 14px 20px;
  background: rgb(255, 255, 255);
  border: 1px solid rgb(229, 231, 233);
  border-right: none;
  & > p {
    color: #757f91;
    font-weight: 500;
    font-size: 16px;
  }
}

.search-calender--tilde {
  width: 8px;
  height: 20px;
  margin: 0 10px;
  color: rgb(117, 127, 145);
  font-weight: 500;
  font-size: 14px;
}
</style>
