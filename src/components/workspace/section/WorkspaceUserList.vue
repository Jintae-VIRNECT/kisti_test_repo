<template>
  <div>
    <div v-if="list.length > 0" class="grid-container">
      <member-card
        v-for="(userinfo, index) in list"
        :key="index"
        :name="userinfo.name"
        :email="userinfo.mail"
        :role="userinfo.role"
        :license="userinfo.license"
      >
      </member-card>
    </div>
    <div v-else class="no-list">
      <div class="no-list__img"></div>
      <div class="no-list__title">협업 가능 멤버가 없습니다.</div>
      <div class="no-list__description">협업 멤버를 추가해주세요.</div>
    </div>
  </div>
</template>

<script>
import MemberCard from 'MemberCard'
import sort from 'mixins/admin/adminSort'
export default {
  name: 'WorkspaceUserList',
  mixins: [sort],
  components: {
    MemberCard,
  },
  data() {
    return {
      //userList: [],
      userList: [
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
  computed: {
    list() {
      if (this.searchFilter === '') {
        return this.userList
      }

      const array = []
      for (const list of this.userList) {
        if (list.name.toLowerCase().match(this.searchFilter.toLowerCase())) {
          array.push(list)
        }
      }
      return array
    },
  },
  watch: {
    searchFilter() {},
  },
}
</script>

<style lang="scss">
.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  column-gap: 8px;
  row-gap: 8px;
}
.no-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
  background-color: #29292c;

  .no-list__img {
    width: 191px;
    height: 233px;
    margin-bottom: 30px;
    background-image: url('~assets/image/mdpi_02.svg');
    background-repeat: no-repeat;
  }

  .no-list__title {
    color: rgb(250, 250, 250);
    font-weight: normal;
    font-size: 24px;
    font-family: NotoSansCJKkr-Regular;
    letter-spacing: 0px;
    text-align: center;
  }
  .no-list__description {
    color: rgb(250, 250, 250);
    font-weight: normal;
    font-size: 18px;
    font-family: NotoSansCJKkr-Regular;
    letter-spacing: 0px;
    text-align: center;
    opacity: 50%;
  }
}
</style>
