<template>
  <div class="test">
    <h1 class="test-title">테스트</h1>
    <section class="test-section" style="background-color: #1e1e20;">
      <h2 class="subtitle">Card Flex Test</h2>
      <div class="action-box">
        <div class="component">
          <div class="grid-container">
            <member-card
              v-for="(userinfo, index) in userInfos"
              :key="index"
              :name="userinfo.name"
              :email="userinfo.mail"
              :role="userinfo.role"
              :status="userinfo.status"
              :license="userinfo.license"
            >
            </member-card>
            <member-card
              v-for="(userinfo, index) in userInfos"
              :key="index"
              :name="userinfo.name"
              :email="userinfo.mail"
              :role="userinfo.role"
              :status="userinfo.status"
              :license="userinfo.license"
              :showMessageButton="true"
            >
            </member-card>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>
<script>
import MemberCard from 'components/workspace/modules/MemberCard'
export default {
  data: function() {
    return {
      userInfos: [
        {
          icon: '',
          name: 'Sherry K. Demmer',
          mail: 'SherryKDemmer@armyspy.com',
          role: 'Master',
          license: true,
          status: 'online',
        },
        {
          icon: '',
          name: 'Samantha J. Fenner',
          mail: 'SamanthaJFenner@armyspy.com',
          role: 'Manager',
          license: false,
          status: 'busy',
        },
        {
          icon: '',
          name: '柳沼 和子',
          mail: 'Tuaid1975@rhyta.com',
          role: '',
          license: false,
          status: 'online',
        },
        {
          icon: '',
          name: '펭수',
          mail: 'pengsoo@ebs.com',
          status: 'offline',
        },
        {
          icon: '',
          name: 'Hong Chang',
          mail: 'HongChang@armyspy.com',
          role: 'Master',
          license: true,
          status: 'online',
        },
        {
          icon: '',
          name: 'Sheng Ts ai',
          mail: 'ShengTsai@armyspy.com',
          role: 'Master',
          license: true,
          status: 'online',
        },
        {
          icon: '',
          name: 'Gang Pai',
          mail: 'GangPai@armyspy.com',
          role: 'Manager',
          license: true,
          status: 'online',
        },
        {
          icon: '',
          name: 'Clara Almeida Martins',
          mail: 'ClaraAlmeidaMartins@armyspy.com',
          role: 'Manager',
          license: true,
          status: 'online',
        },
        {
          icon: '',
          name: '모영권',
          mail: 'moyeongkwon@google.com',
          role: 'Manager',
          license: true,
          status: 'online',
        },
        {
          icon: '',
          name: 'Mo Yeong Kwon',
          mail: 'ykmo@virnect.com',
          role: 'Manager',
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
.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  column-gap: 8px;
  row-gap: 8px;
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
</style>
