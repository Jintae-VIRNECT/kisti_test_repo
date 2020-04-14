<template>
  <div class="grid-container">
    <member-card
      v-for="(userinfo, index) in list"
      :key="index"
      :name="userinfo.name"
      :email="userinfo.mail"
      :role="userinfo.role"
      :status="userinfo.status"
      :license="userinfo.license"
    >
    </member-card>
    <div class="no-list" v-if="userList.length === 0">
      <div style="width:500px height:500px">
        <img src="" />
      </div>
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
        // if (list.name.toLowerCase().match(this.searchFilter.toLowerCase())) {
        array.push(list)
        // }
      }
      return array
    },
  },
  watch: {},
}
</script>

<style lang="scss">
.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  column-gap: 8px;
  row-gap: 8px;
}

.no-list__title {
  color: rgb(250, 250, 250);
  font-size: 24px;
  font-family: NotoSansCJKkr-Regular;
  font-weight: normal;
  text-align: center;
  letter-spacing: 0px;
}
.no-list__description {
  color: rgb(250, 250, 250);
  font-size: 18px;
  font-family: NotoSansCJKkr-Regular;
  font-weight: normal;
  text-align: center;
  letter-spacing: 0px;
  opacity: 50%;
}
</style>
