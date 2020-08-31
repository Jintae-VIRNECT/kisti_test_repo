<template>
  <modal
    title="녹화 파일"
    :visible.sync="visibleFlag"
    :beforeClose="beforeClose"
    :width="'58.5714rem'"
    :height="'47rem'"
  >
    <div class="file-list__header">
      <p class="file-list__header--text">녹화 파일 보기</p>
      <div class="file-list__header--tools">
        <icon-button
          :text="'선택 다운로드'"
          :imgSrc="require('assets/img/ic_down_off.svg')"
          :activeImgSrc="require('assets/img/ic_down_on.svg')"
          :active="selectedFiles.length > 0"
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
      <scroller height="30.6429rem">
        <table class="file-list__table">
          <thead class="file-list__thead">
            <tr class="file-list__thead--row">
              <th class="file-list__thead--column toggle">
                <toggle-button
                  size="1.7143rem"
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
                  size="1.7143rem"
                  :active="selectedArray[index]"
                  :activeSrc="require('assets/img/ic_check.svg')"
                  :inactiveSrc="require('assets/img/ic_uncheck.svg')"
                ></toggle-button>
              </td>
              <td class="file-list__tbody--column filename">
                <p>{{ file.filename }}</p>
              </td>
              <td class="file-list__tbody--column">
                <p>{{ file.duration | convertTime }}</p>
              </td>
              <td class="file-list__tbody--column">
                <p>{{ file.size | convertSize }}</p>
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
  filters: {
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
  data() {
    return {
      visibleFlag: false,
      toggleAllFlag: false,
      selectedArray: [],
    }
  },
  computed: {
    selectedFiles() {
      return this.selectedArray
        .map((val, idx) => {
          return val ? this.fileList[idx] : false
        })
        .filter(Boolean)
    },
  },
  watch: {
    async visible(flag) {
      this.initSelectedArray()
      this.visibleFlag = flag
    },
    selectedArray: {
      handler(ary) {
        if (ary.length > 0) {
          this.toggleAllFlag = ary.every(select => select === true)
        }
      },
      deep: true,
    },
    fileList: {
      handler() {
        this.initSelectedArray()
        this.toggleAllFlag = false
      },
      deep: true,
    },
  },

  methods: {
    async download() {
      for (const file of this.selectedFiles) {
        try {
          const data = await downloadRecordFile({
            id: file.recordingId,
          })

          FileSaver.saveAs(
            new Blob([data], {
              type: data.type,
            }),
            file.filename,
          )
        } catch (e) {
          console.error(e)
        }
      }
    },
    async deleteItems() {
      for (const file of this.selectedFiles) {
        const recordingId = file.recordingId
        try {
          await deleteRecordFile({
            id: recordingId,
          })
        } catch (e) {
          console.error(e)
        }
      }
      this.$eventBus.$emit('load::record-list')
    },
    initSelectedArray() {
      this.selectedArray = this.fileList.map(() => false)
    },
    toggleItem(event, index) {
      const toggleData = !this.selectedArray[index]
      this.selectedArray.splice(index, 1, toggleData)
    },
    toggleAll() {
      this.toggleAllFlag = !this.toggleAllFlag
      this.selectedArray = this.fileList.map(() => this.toggleAllFlag)
    },
    beforeClose() {
      this.$emit('update:visible', false)
      this.$eventBus.$emit('close::record-list')
    },
  },

  mounted() {
    this.initSelectedArray()
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
  margin-bottom: 0.9286rem;
}
.file-list__header--tools {
  display: flex;
}

.file-list__header--text {
  color: #757f91;
  font-weight: 500;
  font-size: 1.2857rem;
}

.file-list__table {
  width: 100%;
}

.file-list__thead--row {
  height: 4rem;
  background: #f5f7fa;
  border-top: 1px solid #eaedf3;
}

.file-list__thead--column {
  color: #757f91;
  font-weight: 500;
  font-size: 1.0714rem;
  &.filename {
    width: 23.5714rem;
    text-align: left;
  }
  &.toggle {
    padding-top: 0.3571rem;
  }
}

.file-list__tbody {
  border-right: 1px solid #eaedf3;
  border-left: 1px solid #eaedf3;
}

.file-list__tbody--row {
  height: 4.4286rem;
  border-top: 1px solid #eaedf3;
  border-bottom: 1px solid #eaedf3;

  &:hover {
    cursor: pointer;
  }
}

.file-list__tbody--column {
  color: #757f91;
  font-weight: 500;
  font-size: 1.0714rem;
  text-align: center;

  &.filename {
    color: #0b1f48;
    text-align: left;
  }
  &.toggle {
    padding-top: 0.3571rem;
  }
}

.file-list--button {
  width: 17.5714rem;
  height: 2.8571rem;
  margin: 2.4286rem 20.5rem;
  background: #145ab6;
  border-radius: 2px;

  & > p {
    color: #ffffff;
    font-weight: 500;
    font-size: 1rem;
  }
}
</style>
