<template>
  <div class="test">
    <h1 class="test-title">테스트</h1>
    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">Wide card List Test</h2>
      <div class="action-box">
        <div class="component">
          <div class="list-wrapper">
            <wide-card-extend
              :menu="true"
              v-for="(item, index) in testdata"
              v-bind:key="index"
            >
              <profile
                :image="item.profileImg"
                :imageAlt="item.mainText"
                :mainText="item.mainText"
                :subText="item.subText"
              ></profile>

              <div slot="column1" class="label label--noraml">
                {{ item.entireTime }}
              </div>
              <div slot="column2" class="label label--date">
                {{ item.lastTime }}
              </div>
              <div slot="column3" class="label lable__icon">
                <img class="icon" src="~assets/image/back/mdpi_icon.svg" />
                <span class="text">{{ item.leaderName }}</span>
              </div>
              <button slot="menuPopover"></button>
              <button class="btn" slot="column4">재시작</button>
            </wide-card-extend>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
<script>
import WideCardExtend from 'WideCardExtend'
import Profile from 'Profile'
export default {
  data: function() {
    return {
      testdata: [
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 01',
          subText: '참석자 0명',
          entireTime: '총 이용 시간: 08분 21초',
          lastTime: '2020.02.10',
          leaderName: '리더 : Nari Kim',
          leaderIcon: '',
        },
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 02',
          subText: '참석자 2명',
          entireTime: '총 이용 시간: 10분 21초',
          lastTime: 'Today',
          leaderName: '리더 : Nari K',
          leaderIcon: '',
        },
        {
          profileImg: require('assets/image/back/mdpi_lnb_img_user.svg'),
          mainText: '버넥트 리모트 03',
          subText: '참석자 4명',
          entireTime: '총 이용 시간: 33분 21초',
          lastTime: '2020.02.10',
          leaderName: '리더 : Nari Kasdfs',
          leaderIcon: '',
        },
      ],
    }
  },
  components: { WideCardExtend, Profile },
}
</script>

<style lang="scss" scoped>
.test {
  height: 100%;
  padding: 30px;
}

.text-title {
  margin-bottom: 30px;
  font-weight: 500;
}

.test-section {
  margin-bottom: 30px;
  background-color: #787878;
  border-radius: 10px;

  .subtitle {
    color: #fff;
    font-weight: 500;
  }
  .action-box {
    display: flex;
    .component {
      position: relative;
      width: 100%;
    }

    .props {
      flex-shrink: 0;
      width: 600px;
      &-option {
        display: flex;
        margin-bottom: 10px;
      }
      &-title {
        flex-shrink: 0;
        width: 100px;
        padding-right: 10px;
        color: #fff;
        text-align: right;
      }
      &-options {
        width: 100%;
      }
    }
  }
}

.label {
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}

.label--noraml {
  color: #d2d2d2;
  font-weight: normal;
  font-size: 15px;
  text-align: center;
}

.label--date {
  color: #fafafa;
  font-size: 15px;
  text-align: center;
  opacity: 50%;
}

.label__icon {
  display: flex;
  align-items: center;
  padding-left: 30px;
  opacity: 86%;
  .icon {
    width: 24px;
    height: 24px;
    margin-right: 5px;
  }

  .text {
    color: #fafafa;
    font-size: 14px;
  }
}
</style>
