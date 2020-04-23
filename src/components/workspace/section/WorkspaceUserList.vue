<template>
  <div>
    <div v-if="memberList.length > 0" class="grid-container">
      <member-card
        v-for="userinfo in list"
        :key="userinfo.participantId"
        :name="userinfo.participantName"
        :email="'example@test.com'"
        :role="userinfo.participantRole"
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
import sort from 'mixins/filter'

export default {
  name: 'WorkspaceUserList',
  mixins: [sort],
  components: {
    MemberCard,
  },
  props: {
    memberList: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {}
  },
  computed: {
    list() {
      if (this.searchFilter === '') {
        return this.memberList
      }

      const array = []
      for (const list of this.memberList) {
        if (
          list.participantName
            .toLowerCase()
            .match(this.searchFilter.toLowerCase())
        ) {
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
    color: #fafafa;
    font-size: 24px;
    text-align: center;
  }
  .no-list__description {
    color: #fafafa;
    font-size: 18px;
    text-align: center;
    opacity: 50%;
  }
}
</style>
