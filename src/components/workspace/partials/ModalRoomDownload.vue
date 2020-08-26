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
        <vue2-scrollbar>
          <div>
            <div
              class="download-table__row"
              v-for="(data, idx) of datas"
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
export default {
  name: 'ModalParticipantsInfo',
  data() {
    return {
      datas: [
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
  methods: {},
}
</script>
