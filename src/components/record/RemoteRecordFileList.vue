<template>
  <modal
    title="녹화 파일"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    :width="'820px'"
    :height="'658px'"
  >
    <div class="file-list__header">
      <p class="file-list__header--text">녹화 파일 보기</p>
      <div class="file-list__header--tools">
        <icon-button
          :text="'선택 다운로드'"
          :imgSrc="require('assets/img/ic_down_off.svg')"
          @click="download"
        ></icon-button>
        <icon-button
          :text="'선택 삭제'"
          :imgSrc="require('assets/img/ic_delete.svg')"
          @click="deleteItems"
        ></icon-button>
      </div>
    </div>
    <div>
      <scroller height="429px">
        <table class="file-list__table">
          <thead class="file-list__thead">
            <tr class="file-list__thead--row">
              <th class="file-list__thead--column toggle">
                <toggle-button
                  size="24px"
                  :active="toggleAllFlag"
                  :activeSrc="require('assets/img/ic_check.svg')"
                  :inactiveSrc="require('assets/img/ic_uncheck.svg')"
                  @action="toggleAll"
                ></toggle-button>
              </th>
              <th class="file-list__thead--column filename"><p>파일명</p></th>
              <th class="file-list__thead--column"><p>녹화 시간</p></th>
              <th class="file-list__thead--column"><p>선택 용량</p></th>
            </tr>
          </thead>
          <tbody class="file-list__tbody">
            <tr
              class="file-list__tbody--row"
              v-for="(file, index) in fileList"
              :class="{ active: selectedArray[index] }"
              :key="index"
              @click="toggleItem($event, index)"
            >
              <td class="file-list__tbody--column toggle">
                <toggle-button
                  size="24px"
                  :active="selectedArray[index]"
                  :activeSrc="require('assets/img/ic_check.svg')"
                  :inactiveSrc="require('assets/img/ic_uncheck.svg')"
                ></toggle-button>
              </td>
              <td class="file-list__tbody--column filename">
                <p>{{ file.filename }}</p>
              </td>
              <td class="file-list__tbody--column">
                <p>{{ convertTime(file.duration) }}</p>
              </td>
              <td class="file-list__tbody--column">
                <p>{{ convertSize(file.size) }}</p>
              </td>
            </tr>
          </tbody>
        </table>
      </scroller>
    </div>

    <button class="file-list--button" @click="beforeClose"><p>확인</p></button>
  </modal>
</template>

<script>
import ToggleButton from 'components/modules/ToggleButton'
import Scroller from 'components/modules/Scroller'
import IconButton from 'components/modules/IconButton'
import Modal from 'components/modules/Modal'

import FileSaver from 'file-saver'
import { downloadRecordFile, deleteRecordFile } from 'api/remote/record'

export default {
  components: {
    Scroller,
    ToggleButton,
    IconButton,
    Modal,
  },

  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    fileList: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      visibleFlag: false,
      toggleAllFlag: false,
      selectedArray: [],
    }
  },
  computed: {},
  watch: {
    async visible(flag) {
      this.setSelectedArray()
      this.visibleFlag = flag
    },
    selectedArray: {
      handler(ary) {
        if (ary.length > 0) {
          const allSelected = ary.every(select => {
            return select === true
          })

          if (allSelected) {
            this.toggleAllFlag = true
          } else {
            this.toggleAllFlag = false
          }
        }
      },
      deep: true,
    },
    fileList: {
      handler() {
        // this.setRenderArray()
        this.setSelectedArray()
        this.toggleAllFlag = false
      },
      deep: true,
    },
  },

  methods: {
    async download() {
      for (const [index, val] of this.selectedArray.entries()) {
        if (val) {
          console.log(
            'this.fileList[index].recordingId ::',
            this.fileList[index].recordingId,
          )
          const data = await downloadRecordFile({
            id: this.fileList[index].recordingId,
          })

          FileSaver.saveAs(data)

          // FileSaver.saveAs(
          //   new File([data], {
          //     type: 'application/octet-stream',
          //   }),
          //   'test.mp4',
          //   true,
          // )

          // FileSaver.saveAs(
          //   'https://192.168.6.3:8073/remote/recorder/file/download/ses_WhkdbDiEW2-ki3MR3CY',

          //   'test.mp4',
          // )
        }
      }
    },
    deleteItems() {
      this.selectedArray.forEach((val, index) => {
        if (val) {
          deleteRecordFile({
            id: this.fileList[index].recordingId,
          })

          this.$eventBus.$emit('load::record-list')
        }
      })
    },
    beforeClose() {
      this.$emit('update:visible', false)
      this.$eventBus.$emit('close::record-list')
    },

    setSelectedArray() {
      this.selectedArray = []

      this.fileList.forEach(() => {
        this.selectedArray.push(false)
      })
    },

    toggleItem(event, index) {
      const toggleData = !this.selectedArray[index]
      this.selectedArray.splice(index, 1, toggleData)
    },
    toggleAll() {
      this.selectedArray = []
      this.toggleAllFlag = !this.toggleAllFlag

      this.fileList.forEach(() => {
        this.selectedArray.push(this.toggleAllFlag)
      })
    },
    convertTime(duration) {
      let sec_num = Number.parseInt(duration, 10)
      let hours = Math.floor(sec_num / 3600)
      let minutes = Math.floor((sec_num - hours * 3600) / 60)
      let seconds = sec_num - hours * 3600 - minutes * 60

      let hText = '시 '
      let mText = '분 '
      let sText = '초'

      if (hours === 0 && minutes === 0 && seconds < 1) {
        hours = ''
        hText = ''

        minutes = ''
        mText = ''

        seconds = '0'
      } else {
        if (hours === 0) {
          hours = ''
          hText = ''
        }
        if (minutes === 0) {
          minutes = ''
          mText = ''
        }
        if (seconds === 0) {
          seconds = ''
          sText = ''
        }
      }

      return hours + hText + minutes + mText + seconds + sText
    },
    convertSize(size) {
      const mb = 1048576

      if (size >= mb) {
        size = size / 1024 / 1024
        return `${size.toFixed(1)}MB`
      } else {
        size = size / 1024
        return `${size.toFixed(1)}KB`
      }
    },
  },

  mounted() {
    this.setSelectedArray()
  },
  beforeDestroy() {
    this.selectedArray = []
  },
}
</script>

<style lang="scss">
.file-list__header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 13px;
}
.file-list__header--tools {
  display: flex;
}

.file-list__header--text {
  color: rgb(117, 127, 145);
  font-weight: 500;
  font-size: 18px;
}

.file-list__table {
  width: 100%;
}

.file-list__thead--row {
  height: 56px;
  background: rgb(245, 247, 250);
  border-top: 1px solid rgb(234, 237, 243);
}

.file-list__thead--column {
  color: rgb(117, 127, 145);
  font-weight: 500;
  font-size: 15px;
  &.filename {
    width: 330px;
    text-align: left;
  }
  &.toggle {
    padding-top: 5px;
  }
}

.file-list__tbody {
  border-right: 1px solid rgb(234, 237, 243);
  border-left: 1px solid rgb(234, 237, 243);
}

.file-list__tbody--row {
  height: 62px;
  border-top: 1px solid rgb(234, 237, 243);
  border-bottom: 1px solid rgb(234, 237, 243);
}

.file-list__tbody--column {
  color: rgb(117, 127, 145);
  font-weight: 500;
  font-size: 15px;
  text-align: center;

  &.filename {
    color: rgb(11, 31, 72);
    text-align: left;
  }
  &.toggle {
    padding-top: 5px;
  }
}

.file-list--button {
  width: 246px;
  height: 40px;
  margin: 34px 287px;
  background: rgb(20, 90, 182);
  border-radius: 2px;

  & > p {
    color: rgb(255, 255, 255);
    font-weight: 500;
    font-size: 14px;
  }
}
</style>
