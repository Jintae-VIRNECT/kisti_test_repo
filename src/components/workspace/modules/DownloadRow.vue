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
        @click="download(file)"
      >
        {{ $t('button.download') }}
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'DownloadRow',
  props: {
    file: {
      type: Object,
      default: () => {
        return {}
      },
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
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style
  lang="scss"
  scoped
  src="assets/style/workspace/workspace-workcard.scss"
></style>
