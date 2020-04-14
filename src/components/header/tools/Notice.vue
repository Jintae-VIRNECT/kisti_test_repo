<template>
  <popover
    trigger="click"
    placement="bottom-end"
    popperClass="popover-notice"
    :width="414"
  >
    <toggle-button
      customClass="header-tools__notice"
      slot="reference"
      description="알림"
      :size="34"
      :toggle="false"
      :activeSrc="require('assets/image/call/gnb_ic_notifi_nor.svg')"
      @action="notice"
    ></toggle-button>

    <div>
      <div class="popover-notice__header">
        <span>알림</span>
        <span>Push</span>
      </div>
      <div class="popover-notice__body">
        <scroller :height="400">
          <notice-item
            section="message"
            :info="'Harry Ha 님'"
            :description="'통화요청 부탁드립니다.'"
            :date="'2020.01.20 오전 11:00'"
            :image="require('assets/image/img_user_profile.svg')"
          ></notice-item>
          <notice-item
            section="invite"
            :info="'Harry Ha 님'"
            :description="'참가자로 협업을 요청하였습니다.'"
            :date="'2020.01.20 오전 11:00'"
            btnType="invite"
            :image="require('assets/image/img_user_profile.svg')"
          ></notice-item>
          <notice-item
            section="alert"
            :info="'[협업 참가 실패]'"
            :description="'최대 참가인원이 초과하였습니다.'"
            :date="'2020.01.20 오전 11:00'"
          ></notice-item>
          <notice-item
            section="info"
            :info="'[만료 안내]'"
            :description="'라이선스가 만료 되었습니다.'"
            :date="'2020.01.20 오전 11:00'"
            btnType="license"
          ></notice-item>
          <notice-item
            section="info"
            :info="'[만료 안내]'"
            :description="'라이선스 만료 <em>[60분]</em> 남았습니다.'"
            :date="'2020.01.20 오전 11:00'"
          ></notice-item>
        </scroller>
      </div>
      <div class="popover-notice__footer">
        <span>알림은 30일 동안 보관됩니다.</span>
      </div>
    </div>
  </popover>
</template>

<script>
import Popover from 'Popover'
import ToggleButton from 'ToggleButton'
import Scroller from 'Scroller'
import NoticeItem from './NoticeItem'
export default {
  name: 'Notice',
  components: {
    Popover,
    ToggleButton,
    Scroller,
    NoticeItem,
  },
  data() {
    return {}
  },
  watch: {},
  methods: {
    notice() {
      // this.$nextTick(() => {
      //   console.log(this.$refs['noticeScroller'])
      //   this.$refs['noticeScroller'].scrollReset()
      // })
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss">
.popover.popover-notice {
  overflow: hidden;
  background-color: #242427;
  border: solid 1px rgba(#979797, 0.12);
  border-radius: 4px;
  transform: translateY(20px);
  > .popover--body {
    padding: 0;
  }
}
.popover-notice__header {
  padding: 11px 16px;
  border-bottom: solid 1px rgba(#eaeeee, 0.06);
  > span {
    padding: 11px 12px;
    color: #fff;
    font-size: 16px;
  }
}
.popover-notice__body {
  height: 400px;
  padding-right: 10px;
  border-bottom: solid 1px rgba(#fff, 0.09);
  > .vue-scrollbar__wrapper.popover-notice__scroller {
    height: 400px;
  }
}
.popover-notice__footer {
  padding: 11px 14px;
  background-color: #313134;
  > span {
    color: rgba(#fff, 0.74);
    font-size: 13px;
  }
}
</style>
