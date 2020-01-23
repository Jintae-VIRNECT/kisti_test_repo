<template lang="pug">
	el-row(:gutter='20')
		el-col(:span='6' v-for="(ud, index) in usersData" :key="index")
			member-profile-card(:profileData="ud")
</template>

<script>
import MemberProfileCard from '@/components/members/MemberProfileCard.vue'
import usersData from '@/data/users'

export default {
  components: {
    MemberProfileCard,
  },
  data() {
    return {
      usersData,
    }
  },
}
</script>
