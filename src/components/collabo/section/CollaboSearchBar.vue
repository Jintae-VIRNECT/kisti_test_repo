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
        <div class="collabo-search-bar__condition--header">
          <label class="collabo-search-bar--label">기간 선택</label>
          <div class="collabo-search-bar__condition--divider"></div>
          <check-box :text="'검색 사용'" :value.sync="useDate"></check-box>
        </div>
        <v-date-picker
          v-model="range"
          :is-range="true"
          :masks="masks"
          :columns="$screens({ default: 2, lg: 2 })"
          :popover="{ visibility: 'click' }"
          @popoverWillShow="toggleCalendarBtn"
          @popoverWillHide="toggleCalendarBtn"
        >
          <template v-slot="{ inputValue, inputEvents, togglePopover }">
            <div class="collabo-search-bar__date">
              <input
                class="collabo-search-bar__date--input"
                :value="inputValue.start + ' ~ ' + inputValue.end"
                v-on="inputEvents.start"
                readonly
              />
              <button
                class="collabo-search-bar__date--buton"
                @click="togglePopover({ placement: 'auto-start' })"
              >
                <img
                  v-if="!calendarBtn"
                  src="~assets/image/calendar/ic_calendar_default.svg"
                  alt="calendar_hide"
                />
                <img
                  v-else
                  src="~assets/image/calendar/ic_calendar_active.svg"
                  alt="calendar_active"
                />
              </button>
            </div>
          </template>
        </v-date-picker>
      </div>

      <div class="collabo-search-bar__condition">
        <label class="collabo-search-bar--label">
          {{ $t('search.text') }}</label
        >
        <input
          readonly
          class="collabo-search-bar--input"
          type="text"
          :placeholder="$t('search.placeholder')"
          v-on:input="searchText = $event.target.value"
        />
      </div>
      <div class="collabo-search-bar__condition">
        <button
          v-on:keyup.enter="doSearch"
          @click="doSearch"
          class="collabo-search-bar--submit"
        >
          <span>SEARCH</span>
        </button>
      </div>
    </div>
  </section>
</template>

<script>
import CheckBox from 'CheckBox'
import DSelect from 'DashBoardSelect'
import confirmMixin from 'mixins/confirm'
import { mapGetters, mapActions } from 'vuex'
export default {
  name: 'CollaboSearchBar',
  mixins: [confirmMixin],
  components: {
    CheckBox,
    DSelect,
  },
  data() {
    let startDay = new Date()
    let endDay = new Date()
    endDay.setDate(endDay.getDate() - 7)
    return {
      collaboSatus: null,
      searchText: '',
      useDate: false,

      date: {
        from: null,
        to: null,
      },

      range: {
        start: startDay,
        end: endDay,
      },

      masks: {
        input: 'YYYY-MM-DD',
        title: 'YYYY-MM',
      },

      calendarBtn: false,
    }
  },
  computed: {
    ...mapGetters(['calendars']),

    searchOpts() {
      return [
        {
          value: 'all',
          text: this.$t('status.all'),
        },
        {
          value: 'ing',
          text: this.$t('status.progress'),
        },
        {
          value: 'end',
          text: this.$t('status.finished'),
        },
      ]
    },
  },
  watch: {
    calendars: {
      handler() {
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
    range: {
      handler(range) {
        this.setSearch({
          date: {
            from: this.$dayjs(range.start).format('YYYY-MM-DD'),
            to: this.$dayjs(range.end).format('YYYY-MM-DD'),
          },
        })
      },
      deep: true,
    },
  },
  methods: {
    ...mapActions(['setSearch']),
    fromDate() {
      const index = this.calendars.findIndex(cal => cal.name === 'search-from')
      if (index < 0) return null

      this.date.from = this.calendars[index].date
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
      this.date.to = this.calendars[index].date
      this.setSearch({
        date: {
          from: this.date.from,
          to: this.date.to,
        },
      })
    },
    doSearch(e) {
      if (e.type === 'keypress' && e.key === 'Enter') {
        this.$eventBus.$emit('reload::list')
      } else if (e.type === 'click') {
        this.$eventBus.$emit('reload::list')
      }
    },
    resetCondition() {
      this.setSearch({
        date: {
          from: null,
          to: null,
        },
        useDate: { useDate: false },
        input: {
          text: '',
        },
      })
    },
    toggleCalendarBtn() {
      this.calendarBtn = !this.calendarBtn
    },
  },

  mounted() {
    window.addEventListener('keypress', this.doSearch)
  },
  beforeDestroy() {
    this.resetCondition()
    window.removeEventListener('keypress', this.doSearch)
  },
}
</script>

<style lang="scss">
.collabo-search-bar {
  margin-top: 3rem;
}

.collabo-search-bar__wrapper {
  display: flex;
  align-items: center;
  justify-content: space-evenly;
  height: 156px;
  background: rgb(226, 235, 250);
  border: 1px solid rgb(49, 139, 255);
  border-radius: 10px;
  box-shadow: 0px 16px 12px 0px rgba(0, 113, 255, 0.05);
}

.collabo-search-bar__condition {
  display: flex;
  flex-direction: column;
  align-items: center;

  &.padding {
    padding-bottom: 0.3571rem;
  }

  .datepicker {
    width: 353px;
    height: 48px;

    .vdp-datepicker {
      width: 305px;
    }

    .vdp-datepicker--input {
      width: 305px;
      height: 48.0004px;
      border: 1px solid #c2c6ce;
    }
  }

  .calendar-button {
    height: 48.0004px;
    border: 1px solid #c2c6ce;
  }

  .checkbox {
    align-items: center;
    // margin-left: 0.4286rem;

    padding: 0;
    > .checkbox-toggle {
      width: 18px;
      height: 18px;

      &.toggle {
        &:after {
          position: absolute;
          top: 4.002px;
          left: 3.002px;
          width: 8px;
          height: 3px;
          border-bottom: solid 2px #ffffff;
          border-left: solid 2px #ffffff;
          transform: rotate(-45deg);
          content: '';
        }
      }
    }
    > .checkbox-text {
      margin-left: 8px;
      color: rgb(67, 75, 88);
      font-weight: 500;
      font-size: 12px;
      opacity: 0.7;
    }
  }
}

.collabo-search-bar--input {
  width: 492px;
  height: 46px;
  padding: 0 0 0 1.1429rem;
  color: #0b1f48;
  font-weight: 500;
  font-size: 15px;
  letter-spacing: 0px;
  background: #ffffff;
  border: 1px solid #c2c6ce;
  border-radius: 6px;
  outline: none;
  &:hover,
  &:focus {
    border: 1px solid rgb(15, 117, 245);
  }
  transition: 0.3s;

  &::placeholder {
    // padding-left: 1.1429rem;
    color: #bac2cc;
    font-weight: 500;
    font-size: 15px;
  }
}

.collabo-search-bar__status--list.popover--wrapper {
  > .select-label {
    width: 154px;
    min-width: 10.1429rem;
    height: 48px;
    color: rgb(11, 31, 72);
    background: rgb(255, 255, 255);
    border: 1px solid rgb(186, 194, 204);
    border-radius: 6px;

    &:hover {
      border: 1px solid rgb(15, 117, 245);
      border-radius: 6px;
    }
  }
}

.collabo-search-bar--label {
  width: 100%;
  margin-bottom: 11px;
  color: #0b1f48;
  font-weight: 500;
  font-size: 15.0006px;
  text-align: left;
  opacity: 0.9;
}

.collabo-search-bar__condition--header {
  display: flex;
  align-items: center;
  width: 100%;
  margin-bottom: 11px;
  > .collabo-search-bar--label {
    width: 59px;
    margin: 0;
  }
}

.collabo-search-bar--submit {
  width: 140px;
  height: 48px;
  margin-top: 35px;
  font-weight: 500;
  font-size: 1.0714rem;
  background: rgb(10, 90, 191);
  border-radius: 6px;
  transition: 0.3s;
  &:hover {
    background: #0f75f5;
  }
}

.collabo-search-bar__condition--divider {
  width: 1px;
  height: 16px;
  margin: 0px 12.5px;
  background: rgb(32, 51, 89);
  opacity: 0.2;
}

.collabo-search-bar__date {
  display: flex;
  width: 353px;
  height: 48px;
  border: 1px solid rgb(186, 194, 204);
  border-radius: 6px;
  &:hover,
  &:focus {
    border: 1px solid rgb(15, 117, 245);
    > .collabo-search-bar__date--buton {
      border-left: 1px solid rgb(15, 117, 245);
    }
  }
  transition: 0.3s;
}

.collabo-search-bar__date--input {
  width: 100%;
  height: 100%;
  padding: 0px 0px 0px 14.5px;
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 15px;
  border: none;
  border-radius: 6px 0px 0px 6px;
}

.collabo-search-bar__date--buton {
  min-width: 48px;
  height: 46x;
  background: rgb(255, 255, 255);
  border-left: 1px solid rgb(186, 194, 204);
  border-radius: 0px 6px 6px 0px;
  transition: 0.3s;
  &:hover,
  &:focus {
    border-left: 1px solid rgb(15, 117, 245);
  }
}

.vc-pane-container {
  width: 600px;
  height: 313px;
}
</style>
