<template>
  <div class="test">
    <h1 class="test-title">테스트</h1>
    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">Card Flex Test</h2>
      <div class="action-box">
        <div class="component">
          <member-card></member-card>
        </div>
      </div>
    </section>
  </div>
</template>
<script>
import MemberCard from 'MemberCard'
export default {
  data: function() {
    return {
      userInfors: [
        {
          icon: '',
          name: '버넥트 리모트 01',
          mail: 'example@example.com',
          level: 'Master',
        },
        {
          icon: '',
          name: '버넥트 리모트 02',
          mail: 'example@example.com',
          level: 'Manager',
        },
        {
          icon: '',
          name: '버넥트 리모트 03',
          mail: 'example@example.com',
          level: 'Expired',
        },
        {
          icon: '',
          name: '버넥트 리모트 03',
          mail: 'example@example.com',
          level: 'none',
        },
      ],
    }
  },
  components: {
    MemberCard,
  },
}
</script>

<style lang="scss" scoped>
.test {
  height: 100%;
  padding: 30px;
  // background-color: #bebebe;
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
