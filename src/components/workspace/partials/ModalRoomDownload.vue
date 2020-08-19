<template>
  <section class="roominfo-view">
    <p class="roominfo-view__title">
      {{ '다운로드(작업중)' }}
    </p>
    <div class="roominfo-view__body">
      <table class="download-table">
        <tr class="download-table__header">
          <th>이름</th>
          <th>기간</th>
          <th></th>
        </tr>
        <tr
          class="download-table__body"
          v-for="(data, idx) of datas"
          :key="'download_' + idx"
        >
          <td>{{ data.name }}</td>
          <td>{{ data.time }}</td>
          <td><button class="btn small">다운로드</button></td>
        </tr>
      </table>
    </div>
  </section>
</template>

<script>
import Scroller from 'Scroller'
export default {
  name: 'ModalParticipantsInfo',
  components: {
    Scroller,
  },
  data() {
    return {
      datas: [
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
      ],
    }
  },
  methods: {},
}
</script>
