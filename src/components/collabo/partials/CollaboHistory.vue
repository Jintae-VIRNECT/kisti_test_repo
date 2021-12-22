<template>
  <section class="history" :class="{ loading: loading }">
    <div class="history__header">
      <div class="history__header--text index">
        <span @click="setSort('NO')" :class="{ active: sort.column === 'NO' }"
          >No</span
        >
      </div>
      <div class="history__header--text collabo-name">
        <span
          @click="setSort('TITLE')"
          :class="{ active: sort.column === 'TITLE' }"
        >
          {{ $t('list.room_title') }}
        </span>
      </div>
      <div class="history__header--text leader-name">
        <span
          :class="{ active: sort.column === 'LEADER_NICK_NAME' }"
          @click="setSort('LEADER_NICK_NAME')"
          >{{ $t('list.room_leader') }}</span
        >
      </div>
      <div
        class="history__header--text start-date"
        :class="{ 'hide-tablet': showFileInfo }"
      >
        <span
          @click="setSort('ACTIVE_DATE')"
          :class="{ active: sort.column === 'ACTIVE_DATE' }"
          >{{ $t('list.room_active_date') }}</span
        >
      </div>
      <div
        class="history__header--text state"
        :class="{ 'hide-tablet': showFileInfo }"
      >
        <span
          @click="setSort('STATUS')"
          :class="{ active: sort.column === 'STATUS' }"
          >{{ $t('list.room_status') }}</span
        >
      </div>

      <div
        v-if="useServerRecord && allowFileInfo"
        class="history__header--text count"
      >
        <span
          @click="setSort('SERVER_RECORD_FILE_COUNT')"
          :class="{ active: sort.column === 'SERVER_RECORD_FILE_COUNT' }"
          >{{ $t('list.room_server_record') }}</span
        >
      </div>
      <div
        v-if="useLocalRecord && allowFileInfo && isOnpremise"
        class="history__header--text count"
      >
        <span
          @click="setSort('LOCAL_RECORD_FILE_COUNT')"
          :class="{ active: sort.column === 'LOCAL_RECORD_FILE_COUNT' }"
          >{{ $t('list.room_local_record') }}</span
        >
      </div>
      <div
        v-if="useStorage && allowFileInfo"
        class="history__header--text count"
      >
        <span
          @click="setSort('ATTACHED_FILE_COUNT')"
          :class="{ active: sort.column === 'ATTACHED_FILE_COUNT' }"
          >{{ $t('list.room_attach_file') }}</span
        >
      </div>
    </div>

    <div class="history__body" :class="{ nodata: !listExists }">
      <span v-if="loading" class="history__body--loading"></span>
      <template v-else-if="!loading && listExists">
        <div
          class="history__row"
          v-for="(history, index) in historys"
          :key="index"
          @click="showHistory(history.sessionId, history.status)"
          @mouseover="toggleHover(true, index)"
          @mouseleave="toggleHover(false, index)"
        >
          <div class="history__text index">
            <p>{{ history.no }}</p>
          </div>
          <div class="history__text collabo-name">
            <p>{{ history.title }}</p>
          </div>
          <div class="history__text leader-name">
            <p>
              {{ leaderName(history) }}
            </p>
          </div>
          <div
            class="history__text start-date"
            :class="{ 'hide-tablet': showFileInfo }"
          >
            {{ date(history.activeDate) }}
          </div>
          <div
            class="history__text state"
            :class="{ 'hide-tablet': showFileInfo }"
          >
            <collabo-status :status="history.status"> </collabo-status>
          </div>

          <div
            v-if="useServerRecord && allowFileInfo"
            class="history__text count"
          >
            <count-button
              :count="history.serverRecord"
              :images="{
                select: require('assets/image/ic_rec_select.svg'),
                active: require('assets/image/ic_rec_active.svg'),
                default: require('assets/image/ic_rec_default.svg'),
              }"
              @click="showFiles('server', history, index)"
              :hover="hover && hoverIndex === index"
            ></count-button>
          </div>
          <div
            v-if="useLocalRecord && allowFileInfo && isOnpremise"
            class="history__text count"
          >
            <count-button
              :count="history.localRecord"
              :images="{
                select: require('assets/image/ic_video_select.svg'),
                active: require('assets/image/ic_video_active.svg'),
                default: require('assets/image/ic_video_default.svg'),
              }"
              @click="showFiles('local', history, index)"
              :hover="hover && hoverIndex === index"
            ></count-button>
          </div>
          <div v-if="useStorage && allowFileInfo" class="history__text count">
            <count-button
              :count="history.attach"
              :images="{
                select: require('assets/image/ic_file_select.svg'),
                active: require('assets/image/ic_file_active.svg'),
                default: require('assets/image/ic_file_default.svg'),
              }"
              @click="showFiles('attach', history, index)"
              :hover="hover && hoverIndex === index"
            ></count-button>
          </div>
        </div>
        <history-info
          :sessionId="sessionId"
          :status="historyStatus"
          :visible.sync="historyInfo"
        ></history-info>

        <file-info
          type="server"
          :title="historyTitle"
          :tableTitle="$t('file.server_record')"
          :fileList="fileList"
          :visible.sync="serverRecord"
          :deletable="isMaster"
          :showToggleHeader="true"
          :showPlayButton="true"
        ></file-info>

        <file-info
          type="local"
          :title="historyTitle"
          :tableTitle="$t('file.local_record')"
          :fileList="fileList"
          :visible.sync="localRecord"
          :deletable="isMaster"
          :showToggleHeader="true"
          :showPlayButton="true"
        >
        </file-info>

        <file-info
          type="attach"
          :title="historyTitle"
          :tableTitle="$t('file.attach_file')"
          :fileList="fileList"
          :visible.sync="file"
          :deletable="isMaster"
          :showToggleHeader="true"
          :showPlayButton="false"
        >
        </file-info>
      </template>
      <span v-else class="history__body--nodata">
        <img src="~assets/image/image_no_data.svg" alt="no data" />
        <p>{{ $t('list.no_data') }}</p></span
      >
    </div>
  </section>
</template>

<script>
import CollaboStatus from 'CollaboStatus'
import CountButton from 'CountButton'

import HistoryInfo from 'components/collabo/modal/ModalHistoryInfo'
import FileInfo from 'components/collabo/modal/ModalFileInfo'

import {
  getServerRecordFiles,
  getAttachFiles,
  getLocalRecordFiles,
} from 'api/http/file'

import { mapGetters, mapActions } from 'vuex'
import confirmMixin from 'mixins/confirm'

import { ROLE } from 'configs/remote.config'

export default {
  name: 'CollaboHistory',
  mixins: [confirmMixin],
  components: {
    CountButton,
    CollaboStatus,
    HistoryInfo,
    FileInfo,
  },
  props: {
    historys: {
      type: Array,
      default: () => {},
    },
    isMaster: {
      type: Boolean,
      default: false,
    },
    loading: {
      type: Boolean,
    },
  },
  data() {
    return {
      sessionId: '',
      historyInfo: false,
      historyTitle: '',
      historyStatus: true,

      //modal flag
      file: false,
      serverRecord: false,
      localRecord: false,

      //modal data
      fileList: [],

      hover: false,
      hoverIndex: -1,

      sort: { column: '', direction: '' },
    }
  },
  watch: {
    loading() {
      this.file = false
      this.serverRecord = false
      this.localRecord = false
    },
  },
  computed: {
    ...mapGetters(['useServerRecord', 'useLocalRecord', 'useStorage']),
    listExists() {
      return this.historys.length > 0
    },
    showFileInfo() {
      return (
        this.useServerRecord &&
        this.useLocalRecord &&
        this.useStorage &&
        this.allowFileInfo
      )
    },
  },
  methods: {
    ...mapActions(['setSearch']),
    date(activeDate) {
      return this.$dayjs(activeDate + '+00:00')
        .local()
        .calendar(null, {
          sameDay: 'A h:mm',
          lastDay: this.$t('date.lastday'),
          nextDay: this.$t('date.nextday'),
          lastWeek: 'YYYY.MM.DD',
          sameElse: 'YYYY.MM.DD',
        })
    },
    showHistory(sessionId, status) {
      this.sessionId = sessionId
      this.historyInfo = true
      this.historyStatus = status
    },

    async showFiles(type, history) {
      this.historyTitle = history.title
      this.fileList = await this.loadFiles(type, history)

      switch (type) {
        case 'server':
          this.serverRecord = true
          break
        case 'local':
          this.localRecord = true
          break
        case 'attach':
          this.file = true
          break
      }
    },

    async loadFiles(type, history) {
      let result = null

      const params = {
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
        sessionId: history.sessionId,
      }

      switch (type) {
        case 'server':
          result = this.getServerRecordFile(params)
          break
        case 'local':
          result = this.getLocalRecordFileInfo(params)
          break
        case 'attach':
          result = this.getAttachFileInfo(params)
          break
      }
      return result
    },
    async getAttachFileInfo(params) {
      let files = await getAttachFiles(params)
      let result = files.fileInfoList.filter(info => !info.deleted)
      return result
    },
    async getLocalRecordFileInfo(params) {
      let files = await getLocalRecordFiles(params)
      let result = files.fileDetailInfoList
        .map(info => {
          if (info.durationSec === 0) {
            info.size = 0
          }
          return info
        })
        .filter(info => !info.deleted)
      return result
    },

    async getServerRecordFile(params) {
      let files = await getServerRecordFiles(params)
      let result = files.infos.map(info => {
        if (info.duration === 0) {
          info.size = 0
        }
        return info
      })
      return result
    },

    setSort(column) {
      // return
      if (this.sort.column === column) {
        if (this.sort.direction === '') {
          this.sort.direction = 'ASC'
        } else if (this.sort.direction === 'ASC') {
          this.sort.direction = 'DESC'
        } else if (this.sort.direction === 'DESC') {
          this.sort.direction = ''
          this.sort.column = ''
        }
      } else {
        this.sort.column = column
        this.sort.direction = 'ASC'
      }

      this.setSearch({
        sort: {
          column: this.sort.column === '' ? 'ACTIVE_DATE' : this.sort.column,
          direction: this.sort.direction === '' ? 'DESC' : this.sort.direction,
        },
      })
      this.$eventBus.$emit('reload::list')
    },
    toggleHover(hover, index) {
      this.hover = hover
      this.hoverIndex = index
    },
    leaderName(history) {
      if (history.memberList && history.memberList.length > 0) {
        const leader = history.memberList.find(member => {
          return member.memberType === ROLE.LEADER
        })

        if (leader && leader.nickName) {
          return leader.nickName
        } else {
          return ''
        }
      } else {
        return ''
      }
    },
  },
}
</script>

<style lang="scss">
@import '~assets/style/mixin';
.history {
  position: relative;
  width: 100%;
}

.history__body {
  min-height: 44rem;
  &.nodata {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 30.7143rem;
    background: #eaedf3;
    border: 1px solid $color_border;
    border-radius: 10px;
    box-shadow: 0px 6px 12px 0px rgba(0, 0, 0, 0.05);

    p {
      color: #686868;
      font-weight: normal;
      font-size: 1.4286rem;
      text-align: center;
    }
  }
}

.history__body--loading {
  color: transparent;
  &:after {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 5.7143rem;
    height: 5.7143rem;
    background: center center/40px 40px no-repeat url(~assets/image/loading.gif);
    transform: translate(-50%, -50%);
    content: '';
  }
}

.history__body--nodata {
  color: $color_text_main;
  font-weight: 500;
  font-size: 1.2857rem;
}

.history__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 3.1429rem;
}
.history__header--text {
  color: #4a5361;
  font-weight: 500;
  font-size: 0.8571rem;
  text-align: center;

  & > span {
    &:hover {
      cursor: pointer;
    }

    &.active {
      font-weight: bold;
    }

    &::after {
      display: inline-block;
      width: 1rem;
      height: 0.7143rem;
      background: url(~assets/image/ic_list_up.svg) center/100% no-repeat;
      content: '';
    }
  }

  &.index {
    width: 7.1429rem;
  }

  &.collabo-name {
    width: 21.4286rem;
    text-align: left;
  }

  &.leader-name {
    width: 11.4286rem;
    // max-width: 16.8572rem;
    text-align: left;
  }

  &.start-date {
    width: 14.2857rem;
    margin-right: 4.3571rem;
  }

  &.state {
    width: 8.5714rem;
    text-align: left;
  }

  &.count {
    width: 8.5714rem;
  }
}

.history__row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 5.5714rem;
  margin-bottom: 0.7143rem;
  background: rgb(255, 255, 255);
  border: 1px solid rgb(240, 240, 240);
  border-radius: 4px;
  box-shadow: 0px 6px 12px 0px rgba(0, 0, 0, 0.05);
  transition: 0.3s;

  &:hover {
    background: rgb(245, 249, 255);
    border: 1px solid #93c3ff;
    cursor: pointer;
  }
}
.history__text {
  color: $color_text_main_1000;
  font-weight: 600;
  font-size: 1.0714rem;
  text-align: center;
  opacity: 0.9;

  &:hover {
    cursor: pointer;
  }

  &.index {
    width: 7.1429rem;
    color: #757f91;
    font-weight: 500;
    font-size: 1.0714rem;
    text-align: center;
  }

  &.collabo-name {
    width: 21.4286rem;
    padding-right: 10px;
    text-align: left;

    & > p {
      width: 16.8572rem;
      // width: 100%;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      @include tablet {
        width: 12.8572rem;
      }
    }
  }

  &.leader-name {
    width: 11.4286rem;
    font-weight: normal;
    text-align: left;
    opacity: 0.8;
  }

  &.start-date {
    width: 14.2857rem;
    margin-right: 4.3571rem;
    font-weight: normal;
    font-family: Roboto;
    opacity: 0.6;
  }

  &.state {
    width: 8.5714rem;
    text-align: left;
  }

  &.count {
    width: 8.5714rem;
  }
}

.history__empty {
  width: 100%;
  height: 36.5714rem;
}
</style>
