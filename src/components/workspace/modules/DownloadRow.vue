<template>
  <div class="download-table__row">
    <div class="download-table__column name">
      <span>{{ file.name }}</span>
    </div>
    <div
      class="download-table__column duration"
      :class="{ expire: file.expired }"
    >
      <span>{{ leftDate }}</span>
    </div>
    <div class="download-table__column">
      <button
        class="btn download-table__button"
        :class="{ expire: file.expired }"
        @click="download"
      >
        {{ $t('button.download') }}
      </button>
    </div>
  </div>
</template>

<script>
import { downloadFile } from 'api/http/file'
import { downloadByURL } from 'utils/file'
export default {
  name: 'DownloadRow',
  props: {
    file: {
      type: Object,
      default: () => {
        return {}
      },
    },
    sessionId: {
      type: String,
      default: '',
    },
  },
  computed: {
    leftDate() {
      if (this.file.expired) {
        return this.$t('common.expiration')
      }
      const date = this.file.expirationDate
      const duration = this.$dayjs.duration(
        this.$dayjs(date).diff(this.$dayjs()),
      )
      if (duration.days() > 0) {
        return this.$t('date.left', {
          time: `${duration.days()}${this.$t('date.day')}`,
        })
      } else if (duration.hours() > 0) {
        return this.$t('date.left', {
          time: `${duration.hours()}${this.$t('date.hour')}`,
        })
      } else if (duration.minutes() > 0) {
        return this.$t('date.left', {
          time: `${duration.minutes()}${this.$t('date.minute')}`,
        })
      }
      return this.$t('date.left', {
        time: `${duration.seconds()}${this.$t('date.second')}`,
      })
    },
  },
  methods: {
    async download() {
      const res = await downloadFile({
        objectName: this.file.objectName,
        sessionId: this.sessionId,
        workspaceId: this.workspace.uuid,
        userId: this.account.uuid,
      })

      downloadByURL(res)
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>
