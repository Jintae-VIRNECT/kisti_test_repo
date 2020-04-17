<template>
  <div class="test">
    <h1 class="test-title">테스트</h1>
    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">Avatar Group Test</h2>
      <div class="action-box">
        <div class="component">
          <div>
            <button class="btn" @click="addAvatar">아바타 만들기</button>
            <button class="btn" @click="removeAvatar">아바타 죽이기</button>
          </div>
          <div class="avatar-group">
            <div v-for="(item, index) in avatars" v-bind:key="index">
              <img :src="item.imgSrc" />
            </div>

            <div v-if="overCount > 0" class="avatar-over-counter">
              <p>{{ '+' + overCount }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
<script>
import Profile from 'Profile'
export default {
  computed: {
    avatarCount() {
      console.log('야')
      return this.avatar.length
    },
  },

  data: function() {
    return {
      overCount: 0,
      max: 10,
      //보여줄 아바타 큐
      avatars: [],

      //실제 데이터
      datas: [],
    }
  },
  components: {},
  methods: {
    addAvatar() {
      this.datas.push({
        name: 'asdf',
        imgSrc: require('assets/image/back/mdpi_lnb_img_user.svg'),
      })
      if (this.datas.length < this.max) {
        this.avatars.push({
          imgSrc: require('assets/image/back/mdpi_lnb_img_user.svg'),
        })
      }
      if (this.datas.length > this.max) {
        this.overCount++
      }
    },
    removeAvatar() {
      this.datas.shift()

      if (this.datas.length >= this.max) {
        this.overCount--
      }
      if (this.datas.length < this.max) {
        this.avatars.shift()
      }
    },
  },
}
</script>

<style lang="scss" scoped>
.avatar-group {
  display: flex;
}

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
.avatar-over-counter {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  text-align: center;
  background: rgb(62, 62, 68);
  border: 1px solid rgb(151, 159, 176);
  border-radius: 50%;
  > p {
    color: rgb(255, 255, 255);
    font-weight: 500;
    font-size: 17px;
    font-family: NotoSansCJKkr-Medium;
    line-height: 120px;
    text-align: center;
  }
}
</style>
