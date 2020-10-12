<template>
  <section class="collabo-history-list">
    <div class="collabo-history-list__header">
      <span class="collabo-history-list__header--title">협업 내역 목록</span>
      <span class="collabo-history-list__header--description">
        최근 협업 기록을 보여줍니다.
      </span>
      <button class="collabo-history-list__header--excel">EXCEL</button>
    </div>
    <history :datas="datas"></history>
    <pagination-tool :totalPage="13"></pagination-tool>
  </section>
</template>

<script>
import History from 'components/partials/History'
import PaginationTool from 'components/partials/PaginationTool'
export default {
  name: 'CollaboHistoryList',
  components: {
    History,
    PaginationTool,
  },
  data() {
    return {
      targetId: 0,
      modalVisible: false,
      fileList: [],
      datas: [
        {
          index: 1,
          userName:
            '일이삼사아육칠팔구십일이삼사아육칠팔구십일이삼사아육칠팔구',
          cooperateName: '일이삼사오',
          startDate: '2020.07.30',
          recordCount: 10,
          serialNum: 123456,
          status: 'PROGRESS',
          leader: '일이삼사오육칠팔구십십일',
        },
        {
          index: 2,
          userName: 'test1',
          cooperateName:
            '일이삼사아육칠팔구십일이삼사아육칠팔구asgfasfgsafssdsfdsfdfdsdsfdsfdsfdsfdfdsfdsfdsfddsfsdfafs십',
          startDate: '2020.07.30',
          recordCount: 10,
          serialNum: 12342134,
          status: 'PROGRESS',
          leader: '김관리',
        },
        {
          index: 3,
          userName: 'test1',
          cooperateName: '일이삼사오',
          startDate: '2020.07.30',
          recordCount: 10,
          serialNum: 65467543,
          status: 'PROGRESS',
          leader: '김관리',
        },
        {
          index: 4,
          userName: 'test1',
          cooperateName: '일이삼사오',
          startDate: '2020.07.30',
          recordCount: 10,
          serialNum: 264565196,
          status: 'PROGRESS',
          leader: '김관리',
        },
        {
          index: 5,
          userName: 'test1',
          cooperateName: '일이삼사오',
          startDate: '2020.07.30',
          recordCount: 0,
          serialNum: 745864123,
          status: 'FINISHED',
          leader: '김관리',
        },
        {
          index: 6,
          userName: 'test1',
          cooperateName: '일이삼사오',
          startDate: '2020.07.30',
          recordCount: 999,
          serialNum: 1215,
          status: 'FINISHED',
          leader: '김관리',
        },
        {
          index: 7,
          userName: 'test1',
          cooperateName: '일이ㄴ삼사오',
          startDate: '2020.07.30',
          recordCount: 2,
          serialNum: 4984,
          status: 'FINISHED',
          leader: '김관리',
        },
      ],

      totalPage: 13,
    }
  },
}
</script>

<style lang="scss">
.collabo-history-list {
  margin-top: 41px;
}
.collabo-history-list__header {
  position: relative;
  height: 54px;
}

.collabo-history-list__header--title {
  margin-right: 10px;
  color: rgb(11, 31, 72);
  font-weight: 500;
  font-size: 22px;
}

.collabo-history-list__header--description {
  color: rgb(122, 122, 122);
  font-weight: 500;
  font-size: 15px;
}
.collabo-history-list__header--excel {
  position: absolute;
  right: 0px;
  width: 89px;
  height: 42px;
  color: rgb(15, 117, 245);
  font-weight: 500;
  font-size: 15px;
  line-height: 20px;
  background: rgb(255, 255, 255);
  border: 1px solid rgb(227, 227, 227);
  border-radius: 2px;
}
</style>
