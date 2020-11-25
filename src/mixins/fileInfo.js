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
          columns = [['name'], ['size'], ['nickName'], ['expirationDate']]
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
            { column: 'size', render: this.fileSizeRenderOmit },
          ]
          break
        case 'local':
          renderers = [
            {
              column: 'durationSec',
              render: this.playTimeRender,
            },
            { column: 'size', render: this.fileSizeRenderOmit },
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
    fileSizeRenderOmit(size) {
      const gb = 1073741824 //1 GB

      if (size >= gb) {
        size = size / 1024 / 1024 / 1024
        return `${size.toFixed(1)}GB`
      } else {
        size = size / 1024 / 1024
        return `${size.toFixed(1)}MB`
      }
    },
    fileSizeRender(size) {
      const mb = 1048576 //1 MB
      const gb = 1073741824 //1 GB

      if (size >= gb) {
        size = size / 1024 / 1024 / 1024
        return `${size.toFixed(1)}GB`
      } else if (gb < size && size >= mb) {
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
      let selects = []
      selectedArray.forEach((select, index) => {
        if (select) {
          selects.push(fileList[index])
        }
      })

      //@TODO : 만료된 파일을 삭제할 수 있는지 확인 필요합니다.
      selects = selects.filter(file => {
        if (file.expirationDate) {
          const diff = this.$dayjs().diff(
            this.$dayjs(file.expirationDate),
            'day',
          )
          if (file.expired || diff >= 8) {
            return false
          }
        }
        return true
      })

      return selects
    },

    async download(type, { selectedArray, fileList }) {
      const errorFiles = []
      const downloadFiles = this.getSelectedFile(selectedArray, fileList)
      const downUrl = this.getDownUrl(type)

      if (downloadFiles.length <= 0) return

      const downFunc = async (type, file, downUrl) => {
        const data = await downUrl(this.getDownParams(type, file))

        let link = null
        switch (type) {
          case 'server':
            if (data === null) {
              throw 'URL IS NULL'
            }
            link = document.createElement('a')
            link.href = proxyUrl(data)
            link.setAttribute('type', 'application/octet-stream')
            link.setAttribute('download', file.filename)
            link.click()
            break
          case 'attach':
          case 'local':
            if (data.url === null) {
              throw 'URL IS NULL'
            }
            downloadByURL(data)
            break
        }
      }

      for (const file of downloadFiles) {
        try {
          await downFunc(type, file, downUrl)
        } catch (e) {
          if (e === 'URL IS NULL') {
            errorFiles.push(file.name)
          } else {
            console.error(e)
          }
        }
      }

      this.showErrorFiles(errorFiles)
    },

    getDownUrl(type) {
      let urlApi = null
      switch (type) {
        case 'attach':
          urlApi = getFileDownloadUrl
          break
        case 'server':
          urlApi = getServerRecordFileUrl
          break
        case 'local':
          urlApi = getLocalRecordFileUrl
          break
      }
      return urlApi
    },

    getDownParams(type, file) {
      let params = null
      switch (type) {
        case 'server':
          params = {
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            id: file.recordingId,
          }
          break
        case 'attach':
        case 'local':
          params = {
            objectName: file.objectName,
            sessionId: file.sessionId,
            userId: this.account.uuid,
            workspaceId: file.workspaceId,
          }
          break
      }
      return params
    },

    getDeleteUrl(type) {
      let urlApi = null
      switch (type) {
        case 'attach':
          urlApi = deleteFileItem
          break
        case 'server':
          urlApi = deleteServerRecordFileItem
          break
        case 'local':
          urlApi = deleteLocalRecordFileItem
          break
      }
      return urlApi
    },

    getDeleteParams(type, file) {
      let params = null
      switch (type) {
        case 'server':
          params = {
            workspaceId: this.workspace.uuid,
            userId: this.account.uuid,
            id: file.recordingId,
          }
          break
        case 'attach':
        case 'local':
          params = {
            objectName: file.objectName,
            sessionId: file.sessionId,
            userId: this.account.uuid,
            workspaceId: file.workspaceId,
          }
          break
      }
      return params
    },

    async deleteFile(type, { selectedArray, fileList }) {
      const errorFiles = []
      const deleteFiles = this.getSelectedFile(selectedArray, fileList)
      const deleteUrl = this.getDeleteUrl(type)

      if (deleteFiles.length <= 0) return

      const deleteFunc = async (type, file, deleteUrl) => {
        await deleteUrl(this.getDeleteParams(type, file))
      }

      for (const file of deleteFiles) {
        try {
          await deleteFunc(type, file, deleteUrl)
        } catch (e) {
          //에러코드 정리후에 네트워크 에러인지
          //실제 없는 파일을 지우려고 시도했는지 확인해야함
          errorFiles.push(file.name)
          console.error(e)
        }
      }

      this.showErrorFiles(errorFiles)
      this.$eventBus.$emit('reload::list')
    },

    showErrorFiles(files) {
      if (files.length > 0) {
        this.confirmDefault(
          `${this.$t('confirm.file_not_found')}\n <p> ${files.join('\n')}</p>`,
        )
      }
    },
  },
}

//보라! 이 드러운 코드를!
