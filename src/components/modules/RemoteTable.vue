<template>
  <div class="table">
    <slot></slot>

    <div class="table__column">
      <div class="table__column--toggle" v-if="showToggleHeader">
        <toggle-button
          slot="body"
          :size="34"
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
      <scroller>
        <div
          v-for="(data, index) in renderArray"
          class="table__row"
          :key="index"
        >
          <div v-if="showToggleHeader" class="table__cell--toggle">
            <toggle-button
              slot="body"
              :size="34"
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
  },
  methods: {
    toggleItem(event, index) {
      const toggleData = !this.selectedArray[index]
      this.selectedArray.splice(index, 1, toggleData)
    },
    toggleAll() {
      const tArray = []

      this.toggleAllFlag = !this.toggleAllFlag

      this.datas.forEach(() => {
        tArray.push(this.toggleAllFlag)
      })

      this.selectedArray = tArray
    },
    //for display only columns
    setRenderArray() {
      this.renderArray = []

      this.datas.forEach(obj => {
        const newObj = {}

        if (obj !== null) {
          this.columns.forEach(key => {
            newObj[key] = obj[key]
          })

          this.renderArray.push(newObj)
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

  created() {
    this.setSelectedArray()
    this.setRenderArray()
  },
  mounted() {},
}
</script>

<style lang="scss">
.table {
  height: 100%;
}
.table__column {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 56px;
  border-top: solid;
  border-top-color: rgb(49, 49, 53);
  border-top-width: 1px;
}
.table__column--toggle {
  margin: 19px 19px 19px 22px;
}
.table__column--cell {
  flex-basis: 0;
  flex-grow: 1;
  flex-shrink: 1;
  color: #a7a7a7;
  font-size: 15px;
  font-family: NotoSansCJKkr-Regular;
  letter-spacing: 0.96px;
  text-align: center;
}

.table__body {
  height: 500px;
}

.table__row {
  display: flex;
  align-items: center;
  width: 100%;
  height: 62px;
  margin: 0px 0px 8px 0px;
  background: #29292c;
}
.table__cell {
  flex-basis: 0;
  flex-grow: 1;
  flex-shrink: 1;
  overflow: hidden;
  color: #d3d3d3;
  font-size: 15px;
  letter-spacing: 0.96px;
  white-space: nowrap;
  text-align: center;
  text-overflow: ellipsis;
}
.table__cell--toggle {
  margin: 19px 19px 19px 22px;
}
</style>
