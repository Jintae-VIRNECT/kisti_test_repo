<template>
  <div class="table">
    <slot></slot>

    <div class="table__column">
      <div
        class="table__column--toggle"
        v-show="showToggleHeader && datas.length > 0"
      >
        <toggle-button
          size="1.75em"
          :active="toggleAllFlag"
          :activeSrc="require('assets/image/ic_check.svg')"
          :inactiveSrc="require('assets/image/ic_ckeck_select.svg')"
          @action="toggleAll"
        ></toggle-button>
      </div>
      <div
        v-for="(headerCell, index) in headers"
        class="table__column--cell"
        :class="{
          name: headers[index] === $t('file.name'),
          'hide-tablet': headers[index] === $t('file.record_member'),
          recordtime: headers[index] === $t('file.record_time'),
        }"
        :key="index"
      >
        {{ headerCell }}
      </div>
    </div>
    <div class="table__body">
      <scroller height="31rem" v-if="datas.length > 0">
        <div
          v-for="(data, index) in renderArray"
          class="table__row"
          :class="{ active: selectedArray[index] }"
          :key="index"
          @click="toggleItem($event, index)"
        >
          <div v-if="showToggleHeader" class="table__cell--toggle">
            <toggle-button
              size="1.75em"
              :active="selectedArray[index]"
              :activeSrc="require('assets/image/ic_check.svg')"
              :inactiveSrc="require('assets/image/ic_ckeck_select.svg')"
            ></toggle-button>
          </div>
          <div
            v-for="(value, key, innerIndex) in data"
            class="table__cell"
            :class="{
              name:
                columns[innerIndex][0] === 'name' ||
                columns[innerIndex][0] === 'filename',
              size: columns[innerIndex][0] === 'size',
              'hide-tablet': columns[innerIndex][0] === 'fileUserInfo',
            }"
            :key="innerIndex"
          >
            {{ value }}
          </div>

          <play-button
            class="table__cell play"
            :class="[type]"
            size="2.1429rem"
            v-if="showPlayButton"
            @click="getPlayIndex(index)"
          ></play-button>
        </div>

        <div
          v-for="(data, index) in expiredArray"
          class="table__row expired"
          :key="index + 99"
        >
          <div v-if="showToggleHeader" class="table__cell--toggle">
            <toggle-button
              size="1.714em"
              :disable="true"
              :disableSrc="require('assets/image/ic_over_date.svg')"
            ></toggle-button>
          </div>
          <div
            v-for="(value, key, innerIndex) in data"
            class="table__cell expired"
            :class="{
              'expiration-date': key === 'expirationDate',
              name: columns[innerIndex][0] === 'name',
              size: columns[innerIndex][0] === 'size',
              'hide-tablet': columns[innerIndex][0] === 'fileUserInfo',
            }"
            :key="innerIndex + workspace.uuid"
          >
            {{ value }}
          </div>
        </div>
      </scroller>
      <div v-else class="table__body--empty">
        <p class="table__body--empty-text">{{ emptyText }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import ToggleButton from 'ToggleButton'
import Scroller from 'Scroller'
import PlayButton from 'PlayButton'
import { deepGet } from 'utils/util'

export default {
  name: 'FileTable',
  components: {
    Scroller,
    ToggleButton,
    PlayButton,
  },
  props: {
    type: {
      type: String,
      default: '',
    },
    showToggleHeader: {
      type: Boolean,
      default: false,
    },
    showPlayButton: {
      type: Boolean,
      default: false,
    },
    headers: {
      type: Array,
      default: () => [],
    },
    columns: {
      type: Array,
      default: () => [],
    },
    datas: {
      type: Array,
      default: () => [],
    },
    renderOpts: {
      type: Array,
      default: () => [],
    },
    emptyText: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      selectedArray: [],
      renderArray: [],
      expiredArray: [],
      toggleAllFlag: false,
    }
  },
  computed: {
    // onlyExpired() {
    //   let isOnlyExpired = false
    //   if (this.datas.length > 0) {
    //     isOnlyExpired = this.datas.every(file => {
    //       if (file.expirationDate) {
    //         const diff = this.$dayjs().diff(
    //           this.$dayjs(file.expirationDate),
    //           'day',
    //         )
    //         if (file.expired || diff >= -20) {
    //           return true
    //         } else {
    //           return false
    //         }
    //       } else {
    //         return false
    //       }
    //     })
    //   }
    //   return isOnlyExpired
    // },
  },
  watch: {
    selectedArray: {
      handler(ary) {
        this.$eventBus.$emit('table:selectedarray', ary)

        if (ary.length > 0) {
          this.toggleAllFlag = ary.every(select => select === true)
        }
      },
      deep: true,
    },
    datas: {
      handler() {
        this.setRenderArray()
        this.setSelectedArray()
        this.toggleAllFlag = false
      },
      deep: true,
      immediate: true,
    },
  },
  methods: {
    getPlayIndex(index) {
      this.$emit('play', index)
    },
    toggleItem(event, index) {
      const toggleData = !this.selectedArray[index]
      this.selectedArray.splice(index, 1, toggleData)
    },
    toggleAll() {
      this.selectedArray = []
      this.toggleAllFlag = !this.toggleAllFlag

      this.datas.forEach(() => {
        this.selectedArray.push(this.toggleAllFlag)
      })
    },

    /**
     * copy datas with specific columns and execute render func
     */
    setRenderArray() {
      this.expiredArray = []
      this.renderArray = this.datas.map(data => {
        const newData = {}

        //copy data with specific columns
        this.columns.forEach(key => {
          const value = deepGet(data, key)
          newData[key] = value ? value : ''
        })

        //execute render function
        this.renderOpts.forEach(render => {
          if (Object.prototype.hasOwnProperty.call(newData, render.column)) {
            newData[render.column] = render.render(newData[render.column])
            if (render.column === 'expirationDate') {
              const diff = this.$dayjs().diff(
                this.$dayjs(data.expirationDate),
                'day',
              )

              if (data.expired || diff >= 8) {
                newData['expirationDate'] = this.$t('file.expired')
              }
            }
          }
        })

        if (
          newData['expirationDate'] !== undefined &&
          newData['expirationDate'] === this.$t('file.expired')
        ) {
          this.expiredArray.push(newData)
          return null
        } else {
          return newData
        }
      })
      this.renderArray = this.renderArray.filter(data => data !== null)

      //만료된 데이터를 뒤로 보냄
      this.renderArray.sort((a, b) => {
        if (
          a.expirationDate === this.$t('file.expired') &&
          b.expirationDate !== this.$t('file.expired')
        ) {
          return 1
        } else if (
          a.expirationDate !== this.$t('file.expired') &&
          b.expirationDate === this.$t('file.expired')
        ) {
          return -1
        } else {
          return 0
        }
      })
    },
    setSelectedArray() {
      this.selectedArray = []

      if (this.showToggleHeader) {
        this.datas.forEach(() => {
          this.selectedArray.push(false)
        })
      }
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.table {
  height: 100%;
}

.table__column {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 4rem;
  border-top: solid;
  border-top-color: #dcdcdc;
  border-top-width: 1px;
}
.table__column--toggle {
  display: flex;
  margin: 0 1.3571rem 0 1.5714rem;
}
.table__column--cell {
  flex-basis: 0;
  flex-grow: 1;
  flex-shrink: 1;
  color: #414a59;
  font-weight: normal;
  font-size: 1.0714rem;
  text-align: center;
}

.table__column--cell:nth-child(2) {
  flex-grow: 2;
  text-align: left;
}

.table__body {
  height: 24.4286rem;
}

.table__row {
  display: flex;
  align-items: center;
  width: 100%;
  height: 4.4286rem;
  margin-bottom: 8px;
  background: $color_white;
  border: 1px solid $color_white;
  border-radius: 4px;
  box-shadow: 0px 6px 12px 0px rgba(0, 0, 0, 0.05);
  transition: 0.3s;

  &.active {
    background: #eaf2ff;
    border: 1px solid #3790ff;
  }
  &:hover {
    background: #eaf2ff;
    border: 1px solid #3790ff;
    cursor: pointer;
  }

  &.expired {
    border: none;
    box-shadow: none;
    &:hover {
      background: $color_white;
      cursor: auto;
    }
  }
}

.table__row .table__cell:nth-child(2) {
  flex-grow: 2.1;
  text-align: left;
}

.table__cell {
  flex-basis: 0;
  flex-grow: 1;
  flex-shrink: 1;
  overflow: hidden;
  color: #262626;
  font-weight: normal;
  font-size: 1.0714rem;
  letter-spacing: 0.96px;
  white-space: nowrap;
  text-align: center;
  text-overflow: ellipsis;

  &.name {
    color: #262626;
    font-weight: 500;
  }

  &.size {
    font-weight: normal;
    opacity: 0.6;
  }

  &.expired {
    color: #9097a2;
    opacity: 0.4;

    &.name {
      color: #262626;
      font-weight: normal;
      opacity: 1;
    }
  }

  &.play {
    display: flex;
    justify-content: center;
  }

  &.expiration-date {
    color: #ff5757;
    opacity: 1;
  }
}
.table__cell--toggle {
  display: flex;
  margin: 0 1.3571rem 0 1.5714rem;
}

.table__body--empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 31rem;
}

.table__body--empty-text {
  color: $color_text_main;
  font-weight: 500;
  font-size: 1.1429rem;
}
</style>
