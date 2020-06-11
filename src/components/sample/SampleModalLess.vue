<template>
  <div class="test">
    <h1 class="test-title">테스트</h1>
    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">Modalless</h2>
      <div class="action-box">
        <div class="component">
          <button @click="modalTest = true" style="color: #fff;">
            Show Modal Less pop up
          </button>
          <device-denied :visible.sync="modalTest"></device-denied>
        </div>
      </div>
    </section>
    <section class="test-section select" style="background-color: #1e1e20;">
      <h2 class="subtitle">Modal</h2>
      <div class="action-box">
        <div class="component">
          <button @click="modalTest2 = true" style="color: #fff;">
            레코드 테스트
          </button>
          <service-local-record-list
            :visible.sync="modalTest2"
          ></service-local-record-list>
        </div>
      </div>
    </section>
    <!-- <section class="test-section select" style="background-color: #1e1e20;">
      <h2 class="subtitle">테이블</h2>
      <div class="action-box">
        <div class="component">
          <remote-table
            :showToggleHeader="true"
            :headers="headers"
            :columns="columns"
            :datas="datas"
            :showTools="true"
          >
            <div class="table__header">
              <div class="table__title">
                {{ tableTitle }}
              </div>
              <div class="table__tools">
                <icon-button
                  text="선택 다운로드"
                  :imgSrc="require('assets/image/ic_download.svg')"
                  @click="download"
                ></icon-button>
                <icon-button
                  text="선택 삭제"
                  :imgSrc="require('assets/image/ic_delete.svg')"
                  @click="deleteItems"
                ></icon-button>
              </div>
            </div>
          </remote-table>
        </div>
      </div>
    </section> -->
  </div>
</template>
<script>
import DeviceDenied from 'components/workspace/modal/WorkspaceDeviceDenied'
import ServiceLocalRecordList from 'components/workspace/modal/ServiceLocalRecordList'
import IconButton from 'components/workspace/modules/IconButton'
import RemoteTable from 'RemoteTable'
export default {
  data() {
    return {
      modalTest: false,
      modalTest2: false,
      headers: ['파일명', '녹화된 시간', '파일 용량', '녹화 계정'],
      columns: ['fileName', 'recordLength', 'fileSize', 'accountName'],
      datas: [
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
          uuid: '123123213',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '12분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
          uuid: '456456465',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '1시간 50분 30초',
          fileSize: '12332052.5MB',
          accountName: '가나다라마바사아자카타파하',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
        },
        {
          fileName: '집에보내줘.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
        },
        {
          fileName: 'ㅎㅎ 일하셈.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
        },
        {
          fileName: '오늘도 내일도 힘내다가 말라죽겠지.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
        },
        {
          fileName: '2020-06-11 HH-MM-SS_00.mp4​',
          recordLength: '9분 54초',
          fileSize: '320.5MB',
          accountName: 'Harry Ha',
          uuid: 'asdfsadf',
        },
      ],
      tableTitle: '로컬 녹화 파일 리스트',
      selectedArray: [],
    }
  },
  components: {
    DeviceDenied,
    ServiceLocalRecordList,
    RemoteTable,
    IconButton,
  },
  methods: {
    download() {
      console.log('download :: called')
    },
    deleteItems() {
      console.log('deleteItems :: called')

      this.selectedArray.forEach((selected, index) => {
        if (selected) {
          this.datas[index] = null
        }
      })
      this.datas = this.datas.filter(obj => {
        return obj !== null
      })
    },
    refreshSelectedArray(selectedArray) {
      this.selectedArray = selectedArray
    },
  },
  created() {
    this.$eventBus.$on('table:selectedarray', this.refreshSelectedArray)
  },
  beforeDestroy() {
    this.$eventBus.$off('table:selectedarray')
  },
}
</script>

<style lang="scss" scoped>
.table__title {
  color: #dedede;
  font-weight: normal;
  font-size: 18px;
}

.test {
  height: 100%;
  padding: 30px;
}

.text-title {
  margin-bottom: 30px;
  font-weight: 500;
}

.test-section {
  margin-bottom: 30px;
  background-color: #787878;
  border-radius: 10px;

  .subtitle {
    color: #fff;
    font-weight: 500;
  }
  .action-box {
    display: flex;
    .component {
      position: relative;
      width: 100%;
    }

    .props {
      flex-shrink: 0;
      width: 600px;
      &-option {
        display: flex;
        margin-bottom: 10px;
      }
      &-title {
        flex-shrink: 0;
        width: 100px;
        padding-right: 10px;
        color: #fff;
        text-align: right;
      }
      &-options {
        width: 100%;
      }
    }
  }
}
</style>
