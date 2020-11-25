<template>
  <div class="table">
    <slot></slot>

    <div class="table__column">
      <div
        class="table__column--toggle"
        v-show="showToggleHeader && datas.length > 0"
      >
        <toggle-button
          size="1.714em"
          :active="toggleAllFlag"
          :activeSrc="require('assets/image/ic_check.svg')"
          :inactiveSrc="require('assets/image/ic_uncheck.svg')"
          @action="toggleAll"
        ></toggle-button>
      </div>
      <div
        class="table__column--cell"
        v-for="(headerCell, index) in headers"
        :key="index"
      >
        {{ headerCell }}
      </div>
      <div v-if="showPlayButton" class="table__column--cell"></div>
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
              size="1.714em"
              :active="selectedArray[index]"
              :activeSrc="require('assets/image/ic_check.svg')"
              :inactiveSrc="require('assets/image/ic_uncheck.svg')"
            ></toggle-button>
          </div>
          <div
            v-for="(value, key, innerIndex) in data"
            class="table__cell"
            :class="{ name: innerIndex === 0 }"
            :key="innerIndex"
          >
            {{ value }}
          </div>

          <play-button
            class="table__cell--play"
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
            <div style="width: 1.74em"></div>
          </div>
          <div
            v-for="(value, key, innerIndex) in data"
            class="table__cell expired"
            :class="{ 'expiration-date': key === 'expirationDate' }"
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
  data() {
    return {
      selectedArray: [],
      renderArray: [],
      expiredArray: [],
      toggleAllFlag: false,
    }
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
              if (data.expired) {
                newData['expirationDate'] = this.$t('file.expired')
              }

              if (this.$dayjs().isAfter(data.expirationDate)) {
                newData['expirationDate'] = this.$t('file.expired')
              }
            }
          }
        })

        if (newData['expirationDate'] === this.$t('file.expired')) {
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
  background-color: #f5f7fa;
  border-top: solid;
  border-top-color: #eaedf3;
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
  // color: #a7a7a7;
  color: #414a59;
  font-weight: normal;
  font-size: 1.0714rem;
  text-align: center;
}

.table__column--cell:nth-child(2) {
  flex-grow: 2;
  text-align: left;
  text-indent: 0.714rem;
}

.table__body {
  height: 24.4286rem;
}

.table__row {
  display: flex;
  align-items: center;
  width: 100%;
  height: 4.4286rem;
  border-bottom: 1px solid #eaedf3;
  // margin-bottom: 0.5714rem;
  // background: $color_darkgray_600;
  transition: background-color 0.3s;

  &.active {
    background: #f5f9ff;
  }
  &:hover {
    background: #f5f9ff;
    cursor: pointer;
  }

  &.expired {
    &:hover {
      background: none;
      cursor: auto;
    }
  }
}

.table__row .table__cell:nth-child(2) {
  flex-grow: 2;
  text-align: left;
}

.table__cell {
  flex-basis: 0;
  flex-grow: 1;
  flex-shrink: 1;
  overflow: hidden;
  color: #757f91;
  font-weight: 500;
  font-size: 1.0714rem;
  white-space: nowrap;
  text-align: center;
  text-overflow: ellipsis;

  &.name {
    color: #0b1f48;
  }

  &.expired {
    color: #9097a2;
  }

  &.expiration-date {
    color: #ff5757;
  }
}
.table__cell--toggle {
  display: flex;
  margin: 0 1.3571rem 0 1.5714rem;
}

.table__cell--play {
  // display: flex;
  &.local {
    margin: 0 3.4286rem 0 3.4286rem;
  }
  &.server {
    margin: 0 3.4286rem 0 4.4286rem;
  }
}

.table__body--empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 31rem;
  // background-color: $color_darkgray_600;
}

.table__body--empty-text {
  color: #0b1f48;
  font-weight: 500;
  font-size: 1.1429rem;
}
</style>
