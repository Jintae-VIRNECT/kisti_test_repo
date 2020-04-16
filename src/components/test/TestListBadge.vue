<template>
  <div class="test">
    <h1 class="test-title">테스트</h1>
    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">List Count Badge Test</h2>
      <div class="action-box">
        <div class="component">
          <div style="width: 300px;">
            <button class="btn" @click="addItem">
              리스트 갯수 추가하기</button
            ><br />
            <button class="btn" @click="deleteItem">
              리스트 갯수 줄이기</button
            ><br />
          </div>
        </div>
      </div>
      <list-badge :listCount="listCount"></list-badge>
      <div class="action-box">
        <div class="component">
          <wide-card v-for="(item, index) in listviewSample" v-bind:key="index">
            <profile
              :image="item.image"
              :imageAlt="item.imageAlt"
              :mainText="item.mainText"
              :subText="item.subText"
              :status="item.status"
              :role="item.role"
            ></profile>
          </wide-card>
        </div>
      </div>
    </section>
  </div>
</template>
<script>
import Profile from 'Profile'
import WideCard from 'WideCard'
import ListBadge from 'ListBadge'
export default {
  data: function() {
    return {
      listviewSample: [
        {
          image: require('assets/image/img_user_profile.svg'),
          imageAlt: '버넥트 리모트01',
          mainText: '버넥트 리모트01',
          subText: 'example@example.com',
          status: 'online',
          role: 'Master',
        },
        {
          image: require('assets/image/img_user_profile.svg'),
          imageAlt: '버넥트 리모트02',
          mainText: '버넥트 리모트02',
          subText: 'example@example.com',
          status: 'online',
          role: 'Master',
        },
        {
          image: require('assets/image/img_user_profile.svg'),
          imageAlt: '버넥트 리모트03',
          mainText: '버넥트 리모트03',
          subText: 'example@example.com',
          status: 'online',
          role: 'Master',
        },
      ],
      testText: [
        'G I A N T 펭!',
        '워 오 자이언트 펭tv',
        '워 오 자이언트 펭tv',
        '아아 놀랐다면 미안',
        '이런 펭귄 처음이지',
        '자이언트 펭~펭~에~ (S O O) ',
        '남극에선 혼자였지',
        '남과 다른 덩치',
        '원래 그래 특별하면 외로운 별이 되지',
        '한국에선 노는 게 제일 좋은 펭귄',
        '뿌르르? 삐리리? 암튼 스타라며',
        '노는 건 내가 제일인데 한번 볼래',
        '아 구걸하려던 건 아냐 자이언트 펭tv',
        '음 그래도 구독하면 내가 잘할게',
        '난 너의 평생친구 자이언트 펭수',
        '워~오~ 자이언트 펭tv',
        '워~오~ 자이언트 펭tv',
        '난 하나뿐인 210 자이언트 펭귄 크리에이러 펭펭!',
        'Yo, 자이언트 펭 tv 렛츠 구독!',
      ],
    }
  },
  computed: {
    listCount: function() {
      return this.listviewSample.length
    },
  },
  components: { WideCard, Profile, ListBadge },
  methods: {
    addItem() {
      let min = Math.ceil(0)
      let max = Math.floor(this.testText.length) - 1
      let randNum = Math.floor(Math.random() * (max - min)) + min //최댓값은 제외, 최솟값은 포함
      this.listviewSample.push({
        image: require('assets/image/img_user_profile.svg'),
        imageAlt: this.testText[randNum] + randNum,
        mainText: this.testText[randNum] + randNum,
        subText: 'example@example.com',
        status: 'online',
        role: 'Master',
      })
    },
    deleteItem() {
      this.listviewSample.pop()
    },
    useless() {},
  },
}
</script>

<style lang="scss">
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
</style>
