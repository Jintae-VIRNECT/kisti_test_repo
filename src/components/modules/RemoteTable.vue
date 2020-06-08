<template>
  <div class="table">
    <div class="table__header">
      <div class="table__header--toggle" v-if="showToggleHeader">
        <div>버튼 들어올꺼임</div>
      </div>
      <div
        class="table__header--cell"
        v-for="(headerCell, index) in headers"
        :key="index"
      >
        {{ headerCell }}
      </div>
    </div>
    <div class="table__body">
      <scroller>
        <div v-for="(data, index) in datas" class="table__row" :key="index">
          <div v-if="showToggleHeader" class="table__cell--toggle">
            <toggle-button
              slot="body"
              :size="34"
              :active="activeArray[index]"
              :activeSrc="require('assets/image/ic_check.svg')"
              :inactiveSrc="require('assets/image/ic_uncheck.svg')"
              @action="toggleCheck($event, index)"
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
  data() {
    return {
      activeArray: [],
      renderArray: [],
    }
  },
  props: {
    showToggleHeader: {
      type: Boolean,
      default: true,
    },
    headers: {
      type: Array,
      default: () => ['파일명', '녹화된 시간', '파일 용량', '녹화 계정'],
    },
    columnList: {
      type: Array,
      default: () => ['fileName', 'recordLength', 'fileSize', 'accountName'],
    },
    datas: {
      type: Array,
      default: () => [
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '12분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
          uuid: '2',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
          uuid: '3',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '1시간 50분 30초',
          fileSize: '12332052.5MB',
          accountName: '가나다라마바사아자카타파하',
          uuid: '4',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
          uuid: '5',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
          uuid: '6',
        },
        {
          fileName: '집에보내줘.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
          uuid: '7',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
          uuid: '8',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
          uuid: '9',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
          uuid: '10',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
          uuid: '11',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
          uuid: '12',
        },
      ],
    },
  },
  methods: {
    toggleCheck(event, index) {
      const toggleData = !this.activeArray[index]
      this.activeArray.splice(index, 1, toggleData)
    },
  },
  created() {
    this.datas.forEach(() => {
      this.activeArray.push(true)
    })
  },
  mounted() {
    console.log('table mounted')
    console.log(this)
  },
}
</script>

<style lang="scss">
.table {
  height: 500px;
}
.table__header {
  display: flex;
  align-content: center;
  justify-content: center;
}
.table__header--toggle {
}
.table__header--cell {
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
  height: 250px;
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
</style>
