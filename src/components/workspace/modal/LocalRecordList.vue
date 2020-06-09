<template>
  <modal
    :visible.sync="visibleFlag"
    :title="'로컬 녹화 파일'"
    :showClose="true"
    :width="940"
    :height="708"
    :beforeClose="beforeClose"
  >
    <div class="record-list">
      <div class="record-list__paragraph">
        <p class="paragraph--text">
          ᛫ 완료된 로컬 녹화 파일은 사용하시는 PC의 시스템에 귀속되어 있습니다.
        </p>
        <p class="paragraph--text">
          ᛫ 공용 PC의 경우, 다른 계정에 의해서 녹화된 파일도 보일 수 있습니다.
        </p>
        <p class="paragraph--text">
          ᛫ 중요한 녹화 파일의 경우, [다운로드]를 하신 후 파일 리스트를 삭제하여
          주시기 바랍니다.
        </p>
      </div>

      <div class="record-list__table">
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
  </modal>
</template>
<script>
import Modal from 'Modal'
import RemoteTable from 'RemoteTable'
import IconButton from 'IconButton'

export default {
  data() {
    return {
      visibleFlag: false,
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
    Modal,
    RemoteTable,
    IconButton,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    visible(flag) {
      this.visibleFlag = flag
    },
  },
  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },

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

<style lang="scss">
.record-list {
  width: 100%;
  height: 100%;
}

.record-list__paragraph {
  margin: 0px 0px 44px 0px;
}

.paragraph--text {
  color: #b7b7b7;
  font-size: 14px;
  letter-spacing: 0.9px;
}

.record-list__table {
}

.table__header {
  display: flex;
  justify-content: space-between;
  margin: 0px 0px 17px 0px;
}

.table__title {
  color: #dedede;
  font-weight: normal;
  font-size: 18px;
}
</style>
