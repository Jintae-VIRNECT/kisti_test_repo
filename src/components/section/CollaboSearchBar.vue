<template>
  <section class="collabo-search-bar">
    <div class="collabo-search-bar__wrapper">
      <div class="collabo-search-bar__condition">
        <label class="collabo-search-bar--label">협업 상태</label>
        <d-select
          class="collabo-search-bar__status--list"
          :options="searchOpts"
          value="value"
          text="text"
          :selectedValue.sync="searchTarget"
          :greyarrow="true"
          :sahdow="false"
        ></d-select>
      </div>
      <div class="collabo-search-bar__condition">
        <label class="collabo-search-bar--label">검색어</label>
        <input
          class="collabo-search-bar--input"
          type="text"
          placeholder="협업명 / 멤버를 입력해주세요"
        />
      </div>
      <div class="collabo-search-bar__condition padding">
        <datepicker
          :pickerName="'search-from'"
          :highlighted="highlighted"
        ></datepicker>
        <span class="collabo-search-bar__condition--tilde"></span>
        <datepicker
          :pickerName="'search-to'"
          :highlighted="highlighted"
        ></datepicker>
        <check-box
          :text="'기간 검색 사용'"
          :value.sync="enableDate"
        ></check-box>
      </div>
    </div>
    <button class="collabo-search-bar--submit"><img />검색하기</button>
  </section>
</template>

<script>
import Datepicker from 'Datepicker'
import CheckBox from 'CheckBox'
import DSelect from 'DashBoardSelect'
import { collabo } from 'utils/collabo'
import { mapGetters } from 'vuex'
export default {
  name: 'CollaboSearchBar',
  components: {
    Datepicker,
    CheckBox,
    DSelect,
  },
  data() {
    return {
      searchTarget: null,
      enableDate: false,
      highlighted: {
        from: null,
        to: null,
      },
    }
  },
  computed: {
    ...mapGetters(['calendars']),

    searchOpts() {
      //@Todo i18n
      //i18n을 위해 남겨둔 코드
      // const options = search.map(opt => {
      //   return {
      //     value: opt,
      //     text: 'i18n',
      //   }
      // })
      // return options
      return [
        {
          value: collabo.ALL,
          text: '전체',
        },
        {
          value: collabo.PROGRESS,
          text: '진행중',
        },
        {
          value: collabo.FINISHED,
          text: '종료',
        },
      ]
    },
  },
  watch: {
    calendars: {
      handler(cal) {
        console.log(cal)
        this.fromDate()
        this.toDate()
      },
      deep: true,
    },
  },
  methods: {
    fromDate() {
      const index = this.calendars.findIndex(cal => cal.name === 'search-from')
      if (index < 0) return null
      this.highlighted.from = new Date(this.calendars[index].date)
    },
    toDate() {
      const index = this.calendars.findIndex(cal => cal.name === 'search-to')
      if (index < 0) return null
      this.highlighted.to = new Date(this.calendars[index].date)
    },
  },
}
</script>

<style lang="scss">
.collabo-search-bar {
  margin-top: 42px;
}

.collabo-search-bar__wrapper {
  display: flex;
  justify-content: center;
  height: 108px;
  background: rgb(215, 224, 236);
  border: 1px solid rgb(205, 210, 220);
  border-radius: 0px;
}

.collabo-search-bar__condition {
  display: flex;
  // justify-items: center;
  align-items: center;
  &.padding {
    padding-bottom: 5px;
  }
}
.collabo-search-bar__condition {
  .vdp-datepicker--input {
    height: 48px;
    border: 1px solid rgb(194, 198, 206);
  }
  .calendar-button {
    height: 48px;
    border: 1px solid rgb(194, 198, 206);
  }
}

.collabo-search-bar--input {
  width: 239px;
  height: 44px;
  margin-right: 69px;
  padding-left: 1.1429rem;
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 16px;
  // line-height: 28px;
  letter-spacing: 0px;
  background: rgb(255, 255, 255);
  border: 1px solid rgb(194, 198, 206);
  border-radius: 4px;
  outline: none;
  &:focus {
    width: 237px;
    height: 42px;
    border: 2px solid rgb(15, 117, 245);
  }

  &::placeholder {
    // padding-left: 1.1429rem;
    color: rgb(186, 194, 204);
    font-weight: normal;
    font-size: 16px;
    // line-height: 28px;
    letter-spacing: 0px;
  }
}

.collabo-search-bar__status--list {
  margin-right: 31.5px;
}

.collabo-search-bar__status--list.popover--wrapper {
  > .select-label {
    min-width: 142px;
    height: 48px;
  }
}

.collabo-search-bar--label {
  margin-right: 10px;
  color: rgb(67, 75, 88);
  font-weight: 500;
  font-size: 16px;
}

.collabo-search-bar--submit {
  width: 100%;
  height: 52px;
  background: rgb(15, 117, 245);
  border-radius: 0px;
}

.collabo-search-bar__condition--tilde {
  margin: 0 9px;
  &::after {
    color: rgb(117, 127, 145);
    font-weight: 500;
    font-size: 14px;
    content: '\223C';
  }
}
</style>
