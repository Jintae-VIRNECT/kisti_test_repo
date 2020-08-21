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
                  size="1.714em"
                  :active="true"
                  :activeSrc="require('assets/img/ic_check.svg')"
                  :inactiveSrc="require('assets/img/ic_uncheck.svg')"
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
              :key="index"
            >
              <td class="file-list__tbody--column toggle">
                <toggle-button
                  size="1.714em"
                  :active="false"
                  :activeSrc="require('assets/img/ic_check.svg')"
                  :inactiveSrc="require('assets/img/ic_uncheck.svg')"
                ></toggle-button>
              </td>
              <td class="file-list__tbody--column filename">
                <p>{{ file.filename }}</p>
              </td>
              <td class="file-list__tbody--column">
                <p>{{ file.duration }}</p>
              </td>
              <td class="file-list__tbody--column">
                <p>{{ file.size }}</p>
              </td>
            </tr>
          </tbody>
        </table>
      </scroller>
    </div>

    <button class="file-list--button"><p>확인</p></button>
  </modal>
</template>

<script>
import ToggleButton from 'components/modules/ToggleButton'
import Scroller from 'components/modules/Scroller'
import IconButton from 'components/modules/IconButton'
import Modal from 'components/modules/Modal'
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
      visibleFlag: true,
    }
  },
  watch: {
    async visible(flag) {
      this.visibleFlag = flag
    },
  },

  methods: {
    beforeClose() {
      this.$emit('update:visible', false)
    },
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
