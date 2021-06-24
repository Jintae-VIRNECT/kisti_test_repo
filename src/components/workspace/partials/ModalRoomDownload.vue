<template>
  <section class="roominfo-view">
    <div class="roominfo-view__title">
      <p>
        {{ $t('button.download') }}
      </p>
    </div>
    <div class="roominfo-view__body table">
      <div class="download-table">
        <div class="download-table__header">
          <div class="download-table__column name" style="width: 50%;">
            <span>{{ $t('workspace.info_name') }}</span>
          </div>
          <div class="download-table__column duration">
            <span>{{ $t('workspace.info_dutation') }}</span>
          </div>
          <div class="download-table__column"></div>
        </div>
        <vue2-scrollbar
          ref="downloadScrollbar"
          v-if="files.length > 0"
          :allowReset="false"
        >
          <div>
            <download-row
              v-for="(data, idx) of files"
              :key="'download_' + idx"
              :file="data"
              :sessionId="sessionId"
            ></download-row>
          </div>
        </vue2-scrollbar>
        <div v-else class="download-table__body">
          <div class="download-table__empty">
            <img src="~assets/image/img_nofile.svg" />
            <p>{{ $t('common.no_sharing_file') }}</p>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { getFileList } from 'api/http/file'
import DownloadRow from 'DownloadRow'
export default {
  name: 'ModalParticipantsInfo',
  components: {
    DownloadRow,
  },
  data() {
    return {
      files: [
        // {
        //   name: '긴제목_짱긴제목_말줄임_처리_해야하는데_귀찮_매우_귀찮.pdf',
        //   // name: '중진공_설계도면.pdf',
        //   time: '1일 남음',
        //   link: 'https://virnect.com',
        // },
        // {
        //   name: '중진공_설계도면.pdf',
        //   time: '1일 남음',
        //   link: 'https://virnect.com',
        // },
        // {
        //   name: '기획_v0.1_2020.10.22.pptx',
        //   time: '기간 만료',
        //   link: 'https://virnect.com',
        //   expire: true,
        // },
      ],
    }
  },
  props: {
    sessionId: {
      type: String,
      default: '',
    },
  },
  watch: {
    sessionId(val) {
      if (val.length > 0) {
        this.init()
      }
    },
  },
  methods: {
    async init() {
      const res = await getFileList({
        sessionId: this.sessionId,
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
      })
      this.files = res.fileInfoList
      this.$nextTick(() => {
        if (this.$refs['downloadScrollbar']) {
          this.$refs['downloadScrollbar'].calculateSize()
        }
      })
    },
  },
  mounted() {
    this.init()
  },
}
</script>
