<template>
  <section class="roominfo-view">
    <div class="roominfo-view__title">
      <p>
        {{ '다운로드' }}
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
        <vue2-scrollbar ref="downloadScrollbar">
          <div>
            <div
              class="download-table__row"
              v-for="(data, idx) of files"
              :key="'download_' + idx"
            >
              <div class="download-table__column name">
                <span>{{ data.name }}</span>
              </div>
              <div
                class="download-table__column duration"
                :class="{ expire: data.expire }"
              >
                <span>{{ data.time }}</span>
              </div>
              <div class="download-table__column">
                <button
                  class="btn download-table__button"
                  :class="{ expire: data.expire }"
                  @click="download(data)"
                >
                  {{ $t('button.download') }}
                </button>
              </div>
            </div>
          </div>
        </vue2-scrollbar>
      </div>
    </div>
  </section>
</template>

<script>
import { fileList, downloadFile } from 'api/workspace/call'
export default {
  name: 'ModalParticipantsInfo',
  data() {
    return {
      files: [
        {
          name: '긴제목_짱긴제목_말줄임_처리_해야하는데_귀찮_매우_귀찮.pdf',
          // name: '중진공_설계도면.pdf',
          time: '1일 남음',
          link: 'https://virnect.com',
        },
        {
          name: '중진공_설계도면.pdf',
          time: '1일 남음',
          link: 'https://virnect.com',
        },
        {
          name: '중진공_설계도면.pdf',
          time: '1일 남음',
          link: 'https://virnect.com',
        },
        {
          name: '중진공_설계도면.pdf',
          time: '1일 남음',
          link: 'https://virnect.com',
        },
        {
          name: '중진공_설계도면.pdf',
          time: '1일 남음',
          link: 'https://virnect.com',
        },
        {
          name: '중진공_설계도면.pdf',
          time: '1일 남음',
          link: 'https://virnect.com',
        },
        {
          name: '긴제목_짱긴제목_말.pdf',
          time: '1일 남음',
          link: 'https://virnect.com',
        },
        {
          name: '기획_v0.1_2020.10.22.pptx',
          time: '기간 만료',
          link: 'https://virnect.com',
          expire: true,
        },
        {
          name: '기획_v0.1_2020.10.22.pptx',
          time: '기간 만료',
          link: 'https://virnect.com',
          expire: true,
        },
        {
          name: '기획_v0.1_2020.10.22.pptx',
          time: '기간 만료',
          link: 'https://virnect.com',
          expire: true,
        },
        {
          name: '기획_v0.1_2020.10.22.pptx',
          time: '기간 만료',
          link: 'https://virnect.com',
          expire: true,
        },
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
      const res = await fileList({
        sessionId: this.sessionId,
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
      })
      console.log(res)
      this.files = res.fileInfoList
      this.$nextTick(() => {
        this.$refs['downloadScrollbar'].calculateSize()
      })
    },
    async download(file) {
      console.log(file)
      const res = await downloadFile({
        fileName: file.name,
        sessionId: this.sessionId,
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
      })
      // console.log(new FileReader(res))
      // console.log(res)
      // console.log(window.btoa(res))
      this.downloadLocal(res, file.name)
    },
    blobToDataURL(blob) {
      return new Promise(resolve => {
        var a = new FileReader()
        a.onload = e => {
          resolve(e.target.result)
        }
        a.readAsDataURL(blob)
      })
    },
    async downloadLocal(data, name) {
      console.log(data)
      const dataUrl = await this.byteToArray(data)
      console.log(dataUrl)
      let a = document.createElement('a')
      document.body.appendChild(a)
      a.style = 'display: none'
      a.href = dataUrl
      a.download = name
      a.click()
      window.URL.revokeObjectURL(dataUrl)
    },
    async byteToArray(data) {
      let blob = new Blob([data], { type: 'image/jpeg' })
      console.log(blob)
      let url = await this.blobToDataURL(blob)
      return url
    },
  },
  mounted() {
    this.init()
  },
}
</script>
