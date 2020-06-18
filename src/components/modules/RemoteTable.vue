<template>
  <div class="table">
    <slot></slot>

    <div class="table__column">
      <div
        class="table__column--toggle"
        v-if="showToggleHeader && datas.length > 0"
      >
        <toggle-button
          slot="body"
          :size="'34px'"
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
    </div>
    <div class="table__body">
      <scroller v-if="datas.length > 0">
        <div
          v-for="(data, index) in renderArray"
          class="table__row"
          :class="{ active: selectedArray[index] }"
          :key="index"
        >
          <div v-if="showToggleHeader" class="table__cell--toggle">
            <toggle-button
              slot="body"
              :size="'34px'"
              :active="selectedArray[index]"
              :activeSrc="require('assets/image/ic_check.svg')"
              :inactiveSrc="require('assets/image/ic_uncheck.svg')"
              @action="toggleItem($event, index)"
            ></toggle-button>
          </div>
          <div
            v-for="(value, innerIndex) in data"
            class="table__cell"
            :key="innerIndex"
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
export default {
  name: 'RemoteTable',
  components: {
    Scroller,
    ToggleButton,
  },
  watch: {
    selectedArray: {
      handler(newArray) {
        this.$eventBus.$emit('table:selectedarray', newArray)
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
    },
  },
  data() {
    return {
      selectedArray: [],
      renderArray: [],
      toggleAllFlag: false,
    }
  },
  props: {
    showToggleHeader: {
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
      this.renderArray = []

      this.renderArray = this.datas.map(data => {
        const newData = {}

        //copy data with specific columns
        this.columns.forEach(key => {
          newData[key] = data[key]
        })

        //execute render function
        this.renderOpts.forEach(render => {
          if (newData.hasOwnProperty(render.column)) {
            newData[render.column] = render.render(newData[render.column])
          }
        })
        return newData
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
  border-top-color: $color_darkgray_500;
  border-top-width: 1px;
}
.table__column--toggle {
  margin: 22px 19px 19px 22px;
}
.table__column--cell {
  flex-basis: 0;
  flex-grow: 1;
  flex-shrink: 1;
  color: #a7a7a7;
  font-size: 1.0714rem;
  text-align: center;
}

.table__column--cell:nth-child(2) {
  flex-grow: 2;
  text-align: left;
}

.table__body {
  height: 342px;
}

.table__row {
  display: flex;
  align-items: center;
  width: 100%;
  height: 4.4286rem;
  margin: 0px 0px 0.5714rem 0px;
  background: $color_darkgray_600;

  &.active {
    background: $color_darkgray_500;
  }
  &:hover {
    background: $color_darkgray_500;
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
  color: #d3d3d3;
  font-size: 1.0714rem;
  white-space: nowrap;
  text-align: center;
  text-overflow: ellipsis;
}
.table__cell--toggle {
  margin: 1.6429rem 1.3571rem 1.3571rem 1.5714rem;
}

.table__body--empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 24.4286rem;
  background-color: $color_darkgray_600;
}

.table__body--empty-text {
  color: #a7a7a7;
  font-size: 1.1429rem;
}
</style>
