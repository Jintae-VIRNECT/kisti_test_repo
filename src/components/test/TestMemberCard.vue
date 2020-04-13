<template>
  <div class="test">
    <h1 class="test-title">테스트</h1>
    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">Card Flex Test</h2>
      <div class="action-box">
        <div class="component">
          <div class="grid-container">
            <member-card
              v-for="(userinfo, index) in userInfors"
              :key="index"
              :name="userinfo.name"
              :email="userinfo.mail"
              :role="userinfo.role"
              :status="userinfo.status"
            >
            </member-card>
          </div>
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
          name: '펭!!하!!',
          mail: 'example@example.com',
          role: 'Master',
          license: true,
          status: 'online',
        },
        {
          icon: '',
          name: '버넥트 리모트 02',
          mail: 'example@example.com',
          role: 'Manager',
          license: false,
          status: 'busy',
        },
        {
          icon: '',
          name: '버넥트 리모트 03',
          mail: 'example@example.com',
          role: '',
          license: true,
          status: 'online',
        },
        {
          icon: '',
          name: '버넥트 리모트 03',
          mail: 'example@example.com',
          role: 'none',
          status: 'offline',
        },
        {
          icon: '',
          name: '펭!!하!!',
          mail: 'example@example.com',
          role: 'Master',
          license: true,
          status: 'online',
        },
        {
          icon: '',
          name: '펭!!하!!',
          mail: 'example@example.com',
          role: 'Master',
          license: true,
          status: 'online',
        },
        {
          icon: '',
          name: '펭!!하!!',
          mail: 'example@example.com',
          role: 'Master',
          license: true,
          status: 'online',
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

.grid-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px 10px;
}
</style>
