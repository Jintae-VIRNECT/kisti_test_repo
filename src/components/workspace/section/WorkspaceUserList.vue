<template>
  <div class="grid-container">
    <member-card
      v-for="userinfo in list"
      :key="userinfo.uuid"
      :name="userinfo.nickName"
      :imageUrl="userinfo.profile"
      :email="userinfo.email"
      :role="userinfo.role"
      :license="userinfo.license"
    >
    </member-card>
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
      return this.getFilter(this.memberList, ['nickName'])
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
</style>
