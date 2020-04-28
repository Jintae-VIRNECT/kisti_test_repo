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
@import '~assets/style/vars';

.grid-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(14.286rem, 1fr));
  column-gap: 0.571rem;
  row-gap: 0.571rem;
}
.no-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 28.571rem;
  background-color: $color_darkgray_600;

  .no-list__img {
    width: 13.643rem;
    height: 16.643rem;
    margin-bottom: 2.143rem;
    background-image: url('~assets/image/img_user_empty.svg');
    background-repeat: no-repeat;
  }

  .no-list__title {
    color: $color_text_sub;
    font-size: 1.714rem;
    text-align: center;
  }
  .no-list__description {
    color: $color_text_sub;
    font-size: 1.286rem;
    text-align: center;
    opacity: 50%;
  }
}
</style>
