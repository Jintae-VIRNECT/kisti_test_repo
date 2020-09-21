<template>
  <section class="roominfo-view">
    <p class="roominfo-view__title">
      {{ $t('workspace.info_remote') }}
    </p>
    <div class="roominfo-view__body">
      <template>
        <figure class="roominfo-figure">
          <p class="roominfo-figure__title">
            {{ $t('workspace.remote_name') }}
          </p>
          <p class="roominfo-figure__text">{{ room.title }}</p>
        </figure>
        <figure class="roominfo-figure">
          <p class="roominfo-figure__title">
            {{ $t('workspace.remote_description') }}
          </p>
          <p class="roominfo-figure__text">{{ room.description }}</p>
        </figure>
      </template>
    </div>
    <div class="roominfo-view__footer">
      <div class="roominfo-view__data">
        <span class="data-title">{{ $t('workspace.info_remote_date') }}</span>
        <span class="data-value">{{ room.activeDate | dateFormat }}</span>
      </div>
      <template>
        <div class="roominfo-view__data">
          <span class="data-title">{{
            $t('workspace.info_remote_start_end')
          }}</span>
          <span class="data-value"
            >{{ room.activeDate | timeFormat }}
            &nbsp;/&nbsp;
            {{ room.unactiveDate | timeFormat }}</span
          >
        </div>
        <div class="roominfo-view__data">
          <span class="data-title">{{
            $t('workspace.info_remote_duration')
          }}</span>
          <span class="data-value">{{
            room.durationSec | durationFormat
          }}</span>
        </div>
      </template>
    </div>
  </section>
</template>

<script>
import dayjs from 'dayjs'
export default {
  name: 'ModalRoomInfo',
  components: {},
  props: {
    room: {
      type: Object,
      default: null,
    },
  },
  filters: {
    dateFormat(date) {
      return dayjs(date + '+00:00').format('YYYY.MM.DD')
    },
    timeFormat(time) {
      return dayjs(time + '+00:00').format('HH:mm:ss')
    },
    durationFormat(time) {
      return dayjs(time * 1000)
        .utc()
        .format('HH:mm:ss')
    },
  },
}
</script>
