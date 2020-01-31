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
  data() {
    return {
      usersData: [],
    }
  },
  methods: {
    getUserList(searchData) {
      console.log('run')
      this.axios
        .get('users', {
          params: {
            uuid: this.$store.getters.getUser.uuid,
            search: 'smic',
          },
        })
        .then(res => {
          const { data } = res.data
          this.usersData = data.userInfoList
        })
    },
  },
  created() {
    this.getUserList()
  },
}
</script>
