<template>
  <section class="collabo-search-bar">
    <div class="collabo-search-bar__wrapper">
      <div class="collabo-search-bar__condition">
        <label class="collabo-search-bar--label">{{
          $t('search.collabo_status')
        }}</label>
        <d-select
          class="collabo-search-bar__status--list"
          :options="searchOpts"
          value="value"
          text="text"
          :selectedValue.sync="collaboSatus"
          :greyarrow="true"
          :sahdow="false"
        ></d-select>
      </div>
      <div class="collabo-search-bar__condition">
        <label class="collabo-search-bar--label">
          {{ $t('search.text') }}</label
        >
        <input
          class="collabo-search-bar--input"
          type="text"
          :placeholder="$t('search.placeholder')"
          v-on:input="searchText = $event.target.value"
        />
      </div>
      <div class="collabo-search-bar__condition padding">
        <datepicker
          :pickerName="'search-from'"
          :highlighted="date"
        ></datepicker>
        <span class="collabo-search-bar__condition--tilde"></span>
        <datepicker :pickerName="'search-to'" :highlighted="date"></datepicker>
        <check-box
          :text="$t('search.use_date_search')"
          :value.sync="useDate"
        ></check-box>
      </div>
    </div>
    <button @click="doSearch" class="collabo-search-bar--submit">
      <span>{{ $t('search.execute') }}</span>
    </button>
  </section>
</template>

<script>
import Datepicker from 'Datepicker'
import CheckBox from 'CheckBox'
import DSelect from 'DashBoardSelect'
import confirmMixin from 'mixins/confirm'
import { collabo } from 'utils/collabo'
import { mapGetters, mapActions } from 'vuex'
export default {
  name: 'CollaboSearchBar',
  mixins: [confirmMixin],
  components: {
    Datepicker,
    CheckBox,
    DSelect,
  },
  data() {
    return {
      collaboSatus: null,
      searchText: '',
      useDate: false,
      date: {
        from: null,
        to: null,
      },
    }
  },
  computed: {
    ...mapGetters(['calendars']),

    searchOpts() {
      return [
        {
          value: collabo.ALL,
          text: this.$t('status.all'),
        },
        {
          value: collabo.PROGRESS,
          text: this.$t('status.progress'),
        },
        {
          value: collabo.FINISHED,
          text: this.$t('status.finished'),
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
    collaboSatus: {
      handler(status) {
        this.setSearch({ status: status })
      },
    },
    searchText: {
      handler(searchText) {
        this.setSearch({
          input: {
            text: searchText,
            target: ['title', 'memberList[].nickName'],
          },
        })
      },
    },
    useDate: {
      handler(useDate) {
        this.setSearch({
          useDate: { useDate: useDate },
        })
      },
    },
  },
  methods: {
    ...mapActions(['setSearch']),
    fromDate() {
      const index = this.calendars.findIndex(cal => cal.name === 'search-from')
      if (index < 0) return null
      this.date.from = new Date(this.calendars[index].date)
      this.setSearch({
        date: {
          from: this.date.from,
          to: this.date.to,
        },
      })
    },
    toDate() {
      const index = this.calendars.findIndex(cal => cal.name === 'search-to')
      if (index < 0) return null
      this.date.to = new Date(this.calendars[index].date)
      this.setSearch({
        date: {
          from: this.date.from,
          to: this.date.to,
        },
      })
    },
    doSearch() {
      this.confirmDefault('현재 준비중인 기능입니다.')
      //날짜 사용하는지 체크
      //날짜 사용하면 날짜 적용한 리스트 다시 로드함
      //필터적용
      //페이징
      //그리고 sort는 sort만(새로운 데이터 X, 기존데이터 변경만)
    },
  },
}
</script>

<style lang="scss">
.collabo-search-bar {
  margin-top: 3rem;
}

.collabo-search-bar__wrapper {
  display: flex;
  justify-content: center;
  height: 7.7143rem;
  background: rgb(215, 224, 236);
  border: 1px solid rgb(205, 210, 220);
  border-radius: 0px;
}

.collabo-search-bar__condition {
  display: flex;
  // justify-items: center;
  align-items: center;
  &.padding {
    padding-bottom: 0.3571rem;
  }
}
.collabo-search-bar__condition {
  .vdp-datepicker--input {
    height: 3.4286rem;
    border: 1px solid rgb(194, 198, 206);
  }
  .calendar-button {
    height: 3.4286rem;
    border: 1px solid rgb(194, 198, 206);
  }
}

.collabo-search-bar--input {
  width: 17.0714rem;
  height: 3.1429rem;
  margin-right: 4.9286rem;
  padding-left: 1.1429rem;
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 1.1429rem;
  // line-height: 28px;
  letter-spacing: 0px;
  background: rgb(255, 255, 255);
  border: 1px solid rgb(194, 198, 206);
  border-radius: 4px;
  outline: none;
  &:focus {
    width: 16.9286rem;
    height: 3rem;
    border: 2px solid rgb(15, 117, 245);
  }

  &::placeholder {
    // padding-left: 1.1429rem;
    color: rgb(186, 194, 204);
    font-weight: normal;
    font-size: 1.1429rem;
    // line-height: 28px;
    letter-spacing: 0px;
  }
}

.collabo-search-bar__status--list {
  margin-right: 2.25rem;
}

.collabo-search-bar__status--list.popover--wrapper {
  > .select-label {
    min-width: 10.1429rem;
    height: 3.4286rem;
  }
}

.collabo-search-bar--label {
  margin-right: 0.7143rem;
  color: rgb(67, 75, 88);
  font-weight: 500;
  font-size: 1.1429rem;
}

.collabo-search-bar--submit {
  position: relative;
  width: 100%;
  height: 3.7143rem;
  font-weight: 400;
  font-size: 1.1429rem;
  background-color: #9aa6bd;
  border-radius: 0px;
  transition: 0.3s;
  &:hover {
    background: rgb(15, 117, 245);
  }

  & > span {
    position: relative;
    &::before {
      position: absolute;
      top: 0.1429rem;
      // right: 50px;
      left: -2.1429rem;
      width: 1.5714rem;
      height: 1.5714rem;
      background: 50% url('~assets/image/ic_search.svg') no-repeat;
      content: '';
    }
  }
}

.collabo-search-bar__condition--tilde {
  margin: 0 0.6429rem;
  &::after {
    color: rgb(117, 127, 145);
    font-weight: 500;
    font-size: 1rem;
    content: '\223C';
  }
}
</style>
