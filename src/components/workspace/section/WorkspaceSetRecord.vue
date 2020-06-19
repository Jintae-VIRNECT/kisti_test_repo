<template>
  <section class="setting-section">
    <div class="setting__title">로컬 녹화 설정</div>
    <div class="setting-horizon-wrapper">
      <figure class="setting__figure">
        <p class="setting__label">최대 녹화 시간</p>
        <r-select
          class="setting__r-selecter"
          @changeValue="setRecLength"
          :options="localRecTimeOpt"
          :value="'value'"
          :text="'text'"
          :defaultValue="localRecordLength"
        >
        </r-select>
      </figure>

      <figure class="setting__figure">
        <div class="setting__figure--wrapper">
          <p class="setting__label">녹화 간격</p>
          <popover
            placement="right"
            trigger="hover"
            popperClass="setting__custom-popover"
            width="25.4286rem"
          >
            <div slot="reference" class="setting__tooltip--icon"></div>
            <div class="setting__tooltip--body">
              <p class="setting__tooltip--text">
                장시간 로컬 녹화 파일 생성 시, PC의 부하 발생할 수 있기 때문에
                녹화 파일을 시간 간격으로 나눠서 생성합니다.
              </p>
            </div>
          </popover>
        </div>
        <r-select
          class="setting__r-selecter"
          @changeValue="setRecInterval"
          :options="localRecIntervalOpt"
          :value="'value'"
          :text="'text'"
          :defaultValue="localRecordInterval"
        >
        </r-select>
      </figure>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import Popover from 'Popover'
import { mapGetters, mapActions } from 'vuex'
export default {
  data() {
    return {
      localRecTimeOpt: [
        {
          value: '5',
          text: '5분',
        },
        {
          value: '10',
          text: '10분',
        },
        {
          value: '15',
          text: '15분',
        },
        {
          value: '30',
          text: '30분',
        },
        {
          value: '60',
          text: '60분',
        },
      ],

      localRecIntervalOpt: [
        {
          value: '60',
          text: '1분',
        },
      ],
    }
  },
  components: {
    RSelect,
    Popover,
  },
  computed: {
    ...mapGetters(['localRecordLength', 'localRecordInterval']),
  },
  methods: {
    ...mapActions(['setLocalRecordLength', 'setLocalRecordInterval']),
    setRecLength(newRecLength) {
      this.setLocalRecordLength(newRecLength.value)
    },
    setRecInterval(newInterval) {
      this.setLocalRecordInterval(newInterval.value)
    },
  },
  created() {
    const time = localStorage.getItem('recordingTime')
    if (time) {
      this.setLocalRecordLength(time)
    }

    const interval = localStorage.getItem('recordingInterval')
    if (interval) {
      this.setLocalRecordInterval(interval)
    }
  },
}
</script>
