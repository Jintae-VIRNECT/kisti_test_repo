<template>
  <section class="collabo-search-bar">
    <div class="collabo-search-bar__wrapper">
      <div class="collabo-search-bar__condition status">
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
          :shadow="false"
        ></d-select>
      </div>

      <div class="collabo-search-bar__condition date">
        <div class="collabo-search-bar__condition--header">
          <label class="collabo-search-bar--label">{{
            $t('search.select_period')
          }}</label>
          <div class="collabo-search-bar__condition--divider"></div>
          <check-box
            :text="$t('search.enable_search')"
            :value.sync="useDateOpt"
          ></check-box>
        </div>
        <v-date-picker
          v-model="range"
          :is-range="true"
          :masks="masks"
          :columns="$screens({ default: 2, lg: 2 })"
          :popover="{ visibility: 'click', placement: 'auto-start' }"
          :locale="currentLanguage"
          @popoverWillShow="toggleCalendarBtn"
          @popoverWillHide="toggleCalendarBtn"
        >
          <template v-slot="{ inputValue, inputEvents, togglePopover }">
            <div class="collabo-search-bar__date">
              <input
                class="collabo-search-bar__date--input"
                :class="{ active: calendarBtn }"
                :value="inputValue.start + ' ~ ' + inputValue.end"
                v-on="inputEvents.start"
                readonly
              />
              <button
                class="collabo-search-bar__date--button"
                @click="togglePopover({ placement: 'auto-start' })"
              >
                <img
                  class="off"
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

      <div class="collabo-search-bar__condition text">
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
      <div class="collabo-search-bar__condition submit">
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
import langMixin from 'mixins/language'
import calendarMixin from 'mixins/calendar'
import searchMixin from 'mixins/search'
import { mapGetters, mapActions } from 'vuex'
export default {
  name: 'CollaboSearchBar',
  mixins: [confirmMixin, langMixin, calendarMixin, searchMixin],
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
      useDateOpt: false,

      range: {
        start: startDay,
        end: endDay,
      },

      calendarBtn: false,
    }
  },
  computed: {
    ...mapGetters(['calendars']),

    searchOpts() {
      return [
        {
          value: 'ALL',
          text: this.$t('status.all'),
        },
        {
          value: 'ONGOING',
          text: this.$t('status.progress'),
        },
        {
          value: 'END',
          text: this.$t('status.finished'),
        },
      ]
    },
  },
  watch: {
    collaboSatus(status) {
      this.setSearch({ status: status })
    },
    searchText(searchText) {
      this.setSearch({
        keyword: {
          text: searchText,
        },
      })
    },
    useDateOpt(useDate) {
      this.setSearch({
        useDate: { useDate: useDate },
      })
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
    doSearch(e) {
      const isEnter = e.type === 'keypress' && e.key === 'Enter'
      const isClick = e.type === 'click'

      if (isEnter || isClick) {
        this.$eventBus.$emit('reload::list')
      }
    },
    toggleCalendarBtn() {
      this.calendarBtn = !this.calendarBtn
      if (this.calendarBtn) {
        this.useDateOpt = true
      }
    },
    initDate() {
      let to = new Date()
      let from = new Date()
      from.setDate(from.getDate() - 7)
      this.setSearch({
        date: {
          from: from,
          to: to,
        },
      })
    },
  },

  mounted() {
    this.initDate()
    window.addEventListener('keypress', this.doSearch)
  },
  beforeDestroy() {
    this.resetCondition()
    window.removeEventListener('keypress', this.doSearch)
  },
}
</script>
