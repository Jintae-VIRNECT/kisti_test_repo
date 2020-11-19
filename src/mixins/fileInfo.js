import {
  getFileDownloadUrl,
  deleteFileItem,
  getLocalRecordFileUrl,
  deleteLocalRecordFileItem,
  getServerRecordFileUrl,
  deleteServerRecordFileItem,
} from 'api/http/file'

import { downloadByURL, proxyUrl } from 'utils/file'
import confirmMixin from 'mixins/confirm'

export default {
  mixins: [confirmMixin],
  methods: {
    getHeader(type) {
      let header = []
      switch (type) {
        case 'attach':
          header = [
            this.$t('file.name'),
            this.$t('file.size'),
            this.$t('file.upload_member'),
            this.$t('file.download_period'),
          ]
          break
        case 'server':
          header = [
            this.$t('file.name'),
            this.$t('file.record_time'),
            this.$t('file.size'),
          ]
          break
        case 'local':
          header = [
            this.$t('file.name'),
            this.$t('file.record_time'),
            this.$t('file.size'),
            this.$t('file.record_member'),
          ]
          break
        default:
          break
      }
      return header
    },
    getColumns(type) {
      let columns = []
      switch (type) {
        case 'attach':
          columns = [['name'], ['size'], ['uploader'], ['expirationDate']]
          break
        case 'server':
          columns = [['filename'], ['duration'], ['size']]
          break
        case 'local':
          columns = [
            ['name'],
            ['durationSec'],
            ['size'],
            ['fileUserInfo', 'nickname'],
          ]
          break
      }
      return columns
    },

    //renders
    getRenderer(type) {
      let renderers = []
      switch (type) {
        case 'attach':
          renderers = [
            { column: 'expirationDate', render: this.expirationDateRender },
            { column: 'size', render: this.fileSizeRender },
          ]
          break
        case 'server':
          renderers = [
            {
              column: 'duration',
              render: this.playTimeRender,
            },
            { column: 'size', render: this.fileSizeRender },
          ]
          break
        case 'local':
          renderers = [
            {
              column: 'durationSec',
              render: this.playTimeRender,
            },
            { column: 'size', render: this.fileSizeRender },
          ]
          break
      }
      return renderers
    },
    playTimeRender(playTime) {
      if (playTime === '') {
        playTime = 0
      }
      let sec_num = Number.parseInt(playTime, 10)
      let hours = Math.floor(sec_num / 3600)
      let minutes = Math.floor((sec_num - hours * 3600) / 60)
      let seconds = sec_num - hours * 3600 - minutes * 60

      let hText = this.$t('date.hour')
      let mText = this.$t('date.minute')
      let sText = this.$t('date.second')

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

      return `${hours}${hText} ${minutes}${mText} ${seconds}${sText}`
    },
    fileSizeRender(size) {
      const mb = 1048576 //1 MB

      if (size >= mb) {
        size = size / 1024 / 1024
        return `${size.toFixed(1)}MB`
      } else {
        size = size / 1024
        return `${size.toFixed(1)}KB`
      }
    },
    expirationDateRender(date) {
      return this.$dayjs(date).format('YYYY.MM.DD')
    },

    getSelectedFile(selectedArray, fileList) {
      const selects = []
      selectedArray.forEach((select, index) => {
        if (select) {
          selects.push(fileList[index])
        }
      })
      return selects
    },

    showErrorFiles(files) {
      if (files.length > 0) {
        this.confirmDefault(
          `${this.$t('confirm.file_not_found')}\n <p> ${files.join('\n')}</p>`,
        )
      }
    },

    async downloadItems(selectedArray, fileList) {
      let downloadFiles = []

      downloadFiles = this.getSelectedFile(selectedArray, fileList)

      for (const file of downloadFiles) {
        try {
          const data = await getFileDownloadUrl({
            objectName: file.objectName,
            sessionId: file.sessionId,
            userId: this.account.uuid,
            workspaceId: file.workspaceId,
          })

          downloadByURL(data)
          console.log(data)
        } catch (e) {
          console.error(e)
        }
      }
    },
    async deleteItems(selectedArray, fileList) {
      let deleteFiles = []
      const errorFiles = []

      deleteFiles = this.getSelectedFile(selectedArray, fileList)

      for (const file of deleteFiles) {
        try {
          const result = await deleteFileItem({
            objectName: file.objectName,
            sessionId: file.sessionId,
            userId: this.account.uuid,
            workspaceId: file.workspaceId,
          })
          console.log(result)
        } catch (e) {
          console.error(e)
          errorFiles.push(file.name)
        }
      }
      this.showErrorFiles(errorFiles)
      this.$eventBus.$emit('reload::list')
    },

    async downloadServer(selectedArray, fileList) {
      let downloadFiles = []

      downloadFiles = this.getSelectedFile(selectedArray, fileList)

      for (const file of downloadFiles) {
        try {
          console.log(file)
          const url = await getServerRecordFileUrl({
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            id: file.recordingId,
          })
          const a = document.createElement('a')
          a.href = proxyUrl(url)
          a.setAttribute('type', 'application/octet-stream')
          a.setAttribute('download', file.filename)
          a.click()
        } catch (e) {
          console.error(e)
        }
      }
    },

    async deleteServer(selectedArray, fileList) {
      let deleteFiles = []
      const errorFiles = []

      deleteFiles = this.getSelectedFile(selectedArray, fileList)

      for (const file of deleteFiles) {
        try {
          await deleteServerRecordFileItem({
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            id: file.recordingId,
          })
        } catch (e) {
          console.error(e)
          errorFiles.push(file.filename)
        }
      }

      this.showErrorFiles(errorFiles)
      this.$eventBus.$emit('reload::list')
    },

    async downloadLocal(selectedArray, fileList) {
      let downloadFiles = []

      downloadFiles = this.getSelectedFile(selectedArray, fileList)

      for (const file of downloadFiles) {
        try {
          const data = await getLocalRecordFileUrl({
            objectName: file.objectName,
            sessionId: file.sessionId,
            userId: this.account.uuid,
            workspaceId: file.workspaceId,
          })
          downloadByURL(data)
          console.log(data)
        } catch (e) {
          console.error(e)
        }
      }
    },
    async deleteLocal(selectedArray, fileList) {
      let deleteFiles = []
      const errorFiles = []

      deleteFiles = this.getSelectedFile(selectedArray, fileList)

      for (const file of deleteFiles) {
        try {
          const result = await deleteLocalRecordFileItem({
            objectName: file.objectName,
            sessionId: file.sessionId,
            userId: this.account.uuid,
            workspaceId: file.workspaceId,
          })
          console.log(result)
        } catch (e) {
          console.error(e)
          errorFiles.push(file.name)
        }
      }
      this.showErrorFiles(errorFiles)
      this.$eventBus.$emit('reload::list')
    },
  },
}
