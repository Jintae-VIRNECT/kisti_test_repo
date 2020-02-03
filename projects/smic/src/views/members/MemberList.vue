<template lang="pug">
	el-row(:gutter='20')
		el-col(:span='6' v-for="(ud, index) in usersData" :key="index")
			member-profile-card(:profileData="ud")
</template>

<script>
import MemberProfileCard from '@/components/members/MemberProfileCard.vue'

export default {
  components: {
    MemberProfileCard,
  },
  props: ['query'],
  computed: {
    usersData() {
      return this.$store.getters.getMemberList
    },
  },
  methods: {
    getUserList() {
      this.$store.dispatch('MEMBER_LIST', { search: 'smic' })
    },
  },
  created() {
    this.getUserList()
  },
}
</script>
